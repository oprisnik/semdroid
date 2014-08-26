/*
 * Copyright 2014 Alexander Oprisnik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oprisnik.semdroid.grouper.opcode;

import com.googlecode.dex2jar.DexOpcodes;
import com.googlecode.dex2jar.OdexOpcodes;

/**
 * Opcode grouper V1.
 */
public class V1SimpleOpcodeGrouper extends FieldBasedOpcodeGrouper {

    public static final int OP_IF = DexOpcodes.OP_IF_EQ;
    public static final int OP_GET = DexOpcodes.OP_AGET;
    public static final int OP_PUT = DexOpcodes.OP_APUT;
    public static final int OP_INVOKE = DexOpcodes.OP_INVOKE_VIRTUAL;
    public static final int OP_CMP = DexOpcodes.OP_CMP;
    public static final int OP_MOVE = DexOpcodes.OP_MOVE;
    public static final int OP_CONST = DexOpcodes.OP_CONST;
    public static final int OP_GOTO_OR_SWITCH = DexOpcodes.OP_GOTO;
    public static final int OP_NUM_MATH = DexOpcodes.OP_ADD;
    public static final int OP_BIN_MATH = DexOpcodes.OP_AND;
    public static final int OP_X_TO_Y = DexOpcodes.OP_X_TO_Y;

    @Override
    public int getOpcodeGroup(int opcode) {
        switch (opcode) {
            case DexOpcodes.OP_NOP:
                return OP_IGNORE;
            case DexOpcodes.OP_MOVE:
                return OP_MOVE;
            case DexOpcodes.OP_MOVE_RESULT:
                return OP_MOVE;
            case DexOpcodes.OP_MOVE_EXCEPTION:
                return OP_MOVE;
            case DexOpcodes.OP_RETURN_VOID:
                return OP_IGNORE;
            case DexOpcodes.OP_RETURN:
                return OP_IGNORE;
            case DexOpcodes.OP_CONST:
                return OP_CONST;
            case DexOpcodes.OP_CONST_STRING:
                return OP_CONST;
            case DexOpcodes.OP_CONST_CLASS:
                return OP_CONST;
            case DexOpcodes.OP_MONITOR_ENTER:
                return OP_IGNORE;
            case DexOpcodes.OP_MONITOR_EXIT:
                return OP_IGNORE;
            case DexOpcodes.OP_CHECK_CAST:
                return OP_IGNORE;
            case DexOpcodes.OP_INSTANCE_OF:
                return OP_IGNORE;
            case DexOpcodes.OP_ARRAY_LENGTH:
                return OP_IGNORE;
            case DexOpcodes.OP_NEW_INSTANCE:
                return OP_IGNORE;
            case DexOpcodes.OP_NEW_ARRAY:
                return OP_IGNORE;
            case DexOpcodes.OP_FILLED_NEW_ARRAY:
                return OP_IGNORE;
            case DexOpcodes.OP_FILL_ARRAY_DATA:
                return OP_IGNORE;
            case DexOpcodes.OP_THROW:
                return OP_IGNORE;
            case DexOpcodes.OP_GOTO:
                return OP_GOTO_OR_SWITCH;
            case DexOpcodes.OP_PACKED_SWITCH:
                return OP_GOTO_OR_SWITCH;
            case DexOpcodes.OP_SPARSE_SWITCH:
                return OP_GOTO_OR_SWITCH;
            case DexOpcodes.OP_CMPL:
                return OP_CMP;
            case DexOpcodes.OP_CMPG:
                return OP_CMP;
            case DexOpcodes.OP_CMP:
                return OP_CMP;
            case DexOpcodes.OP_IF_EQ:
                return OP_IF;
            case DexOpcodes.OP_IF_NE:
                return OP_IF;
            case DexOpcodes.OP_IF_LT:
                return OP_IF;
            case DexOpcodes.OP_IF_GE:
                return OP_IF;
            case DexOpcodes.OP_IF_GT:
                return OP_IF;
            case DexOpcodes.OP_IF_LE:
                return OP_IF;
            case DexOpcodes.OP_IF_EQZ:
                return OP_IF;
            case DexOpcodes.OP_IF_NEZ:
                return OP_IF;
            case DexOpcodes.OP_IF_LTZ:
                return OP_IF;
            case DexOpcodes.OP_IF_GEZ:
                return OP_IF;
            case DexOpcodes.OP_IF_GTZ:
                return OP_IF;
            case DexOpcodes.OP_IF_LEZ:
                return OP_IF;
            case DexOpcodes.OP_AGET:
                return OP_GET;
            case DexOpcodes.OP_APUT:
                return OP_PUT;
            case DexOpcodes.OP_IGET:
                return OP_GET;
            case DexOpcodes.OP_IPUT:
                return OP_PUT;
            case DexOpcodes.OP_SGET:
                return OP_GET;
            case DexOpcodes.OP_SPUT:
                return OP_PUT;
            case DexOpcodes.OP_INVOKE_VIRTUAL:
                return OP_INVOKE;
            case DexOpcodes.OP_INVOKE_SUPER:
                return OP_INVOKE;
            case DexOpcodes.OP_INVOKE_DIRECT:
                return OP_INVOKE;
            case DexOpcodes.OP_INVOKE_STATIC:
                return OP_INVOKE;
            case DexOpcodes.OP_INVOKE_INTERFACE:
                return OP_INVOKE;
            case DexOpcodes.OP_NEG:
                return OP_BIN_MATH;
            case DexOpcodes.OP_NOT:
                return OP_BIN_MATH;
            case DexOpcodes.OP_X_TO_Y:
                return OP_X_TO_Y;
            case DexOpcodes.OP_ADD:
                return OP_NUM_MATH;
            case DexOpcodes.OP_SUB:
                return OP_NUM_MATH;
            case DexOpcodes.OP_MUL:
                return OP_NUM_MATH;
            case DexOpcodes.OP_DIV:
                return OP_NUM_MATH;
            case DexOpcodes.OP_REM:
                return OP_NUM_MATH;
            case DexOpcodes.OP_AND:
                return OP_BIN_MATH;
            case DexOpcodes.OP_OR:
                return OP_BIN_MATH;
            case DexOpcodes.OP_XOR:
                return OP_BIN_MATH;
            case DexOpcodes.OP_SHL:
                return OP_BIN_MATH;
            case DexOpcodes.OP_SHR:
                return OP_BIN_MATH;
            case DexOpcodes.OP_USHR:
                return OP_BIN_MATH;
            case DexOpcodes.OP_ADD_INT_LIT_X:
                return OP_NUM_MATH;
            case DexOpcodes.OP_RSUB_INT_LIT_X:
                return OP_NUM_MATH;
            case DexOpcodes.OP_MUL_INT_LIT_X:
                return OP_NUM_MATH;
            case DexOpcodes.OP_DIV_INT_LIT_X:
                return OP_NUM_MATH;
            case DexOpcodes.OP_REM_INT_LIT_X:
                return OP_NUM_MATH;
            case DexOpcodes.OP_AND_INT_LIT_X:
                return OP_BIN_MATH;
            case DexOpcodes.OP_OR_INT_LIT_X:
                return OP_BIN_MATH;
            case DexOpcodes.OP_XOR_INT_LIT_X:
                return OP_BIN_MATH;
            case DexOpcodes.OP_SHL_INT_LIT_X:
                return OP_BIN_MATH;
            case DexOpcodes.OP_SHR_INT_LIT_X:
                return OP_BIN_MATH;
            case DexOpcodes.OP_USHR_INT_LIT_X:
                return OP_BIN_MATH;

            // ODEX:
            case OdexOpcodes.OP_THROW_VERIFICATION_ERROR:
                return OP_IGNORE;
            case OdexOpcodes.OP_EXECUTE_INLINE:
                return OP_IGNORE;
            case OdexOpcodes.OP_INVOKE_SUPER_QUICK:
                return OP_INVOKE;
            case OdexOpcodes.OP_INVOKE_VIRTUAL_QUICK:
                return OP_INVOKE;
            case OdexOpcodes.OP_IGET_QUICK:
                return OP_GET;
            case OdexOpcodes.OP_IPUT_QUICK:
                return OP_PUT;
        }
        return opcode;
    }

}
