/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.openejb.sfsb;

import java.io.Serializable;
import javax.ejb.SessionSynchronization;

import org.apache.geronimo.interceptor.InvocationResult;
import org.apache.openejb.EJBOperation;
import org.apache.openejb.EjbInvocation;
import org.apache.openejb.dispatch.VirtualOperation;
import org.apache.openejb.timer.TimerState;

/**
 * @version $Revision$ $Date$
 */
public class AfterCompletion implements VirtualOperation, Serializable {
    private static final long serialVersionUID = 8843302214008680622L;
    public static final AfterCompletion INSTANCE = new AfterCompletion();

    private AfterCompletion() {}

    public InvocationResult execute(EjbInvocation invocation) throws Throwable {
        boolean comitted = ((Boolean)invocation.getArguments()[0]).booleanValue();
        StatefulInstanceContext ctx = (StatefulInstanceContext) invocation.getEJBInstanceContext();
        boolean oldTimerMethodAvailable = ctx.setTimerState(EJBOperation.EJBCREATE);
        EJBOperation oldOperation = ctx.getOperation();
        try {
            ctx.setOperation(EJBOperation.EJBCREATE);
            ((SessionSynchronization) ctx.getInstance()).afterCompletion(comitted);
        } finally {
            ctx.setOperation(oldOperation);
            TimerState.setTimerState(oldTimerMethodAvailable);
        }
        return invocation.createResult(null);
    }
}
