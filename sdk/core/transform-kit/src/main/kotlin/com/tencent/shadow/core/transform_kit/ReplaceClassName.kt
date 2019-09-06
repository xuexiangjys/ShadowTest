/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.core.transform_kit

import javassist.ClassPool
import javassist.CtClass
import javassist.NotFoundException
import javassist.expr.ExprEditor
import javassist.expr.MethodCall

object ReplaceClassName {
    private var mErrorCount = 0
    private val mNewNames = mutableListOf<String>()

    fun resetErrorCount() {
        mErrorCount = 0
    }

    fun getErrorCount(): Int {
        return mErrorCount
    }

    fun replaceClassName(ctClass: CtClass, oldName: String, newName: String) {
        ctClass.replaceClassName(oldName, newName)
        mNewNames.add(newName)
    }

    fun checkAll(classPool: ClassPool, classes: Set<CtClass>) {
        classes.forEach { ctClass ->
            mNewNames.forEach { newName ->
                ctClass.checkMethodExist(classPool[newName])
            }
        }
    }

    /**
     * 检查ctClass对refClassName引用的方法确实都存在
     */
    private fun CtClass.checkMethodExist(refClass: CtClass) {
        val invokeClass = name
        instrument(object : ExprEditor() {
            override fun edit(m: MethodCall) {
                if (m.className == refClass.name) {
                    try {
                        refClass.getMethod(m.methodName, m.signature)
                    } catch (ignored: NotFoundException) {
                        mErrorCount++
                        System.err.println("类$invokeClass 调用类${refClass.name}的" +
                                "方法m.methodName==${m.methodName} 签名m.signature==${m.signature}不存在")
                    }
                }
            }
        })
    }
}