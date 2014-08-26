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
 * Opcode grouper 2 for crypto code.
 */
public class CryptoOpcodeGrouper2 extends FieldBasedOpcodeGrouper {

    // Define your opcode groups here...
    // e.g. :
    public static final int OP_OTHER = DexOpcodes.OP_MOVE;

    public static final int OP_NEG = DexOpcodes.OP_NEG;
    public static final int OP_X_TO_Y = DexOpcodes.OP_X_TO_Y;
    public static final int OP_NOT = DexOpcodes.OP_NOT;
    public static final int OP_ADD = DexOpcodes.OP_ADD;
    public static final int OP_SUB = DexOpcodes.OP_SUB;
    public static final int OP_MUL = DexOpcodes.OP_MUL;
    public static final int OP_DIV = DexOpcodes.OP_DIV;
    public static final int OP_REM = DexOpcodes.OP_REM;
    public static final int OP_AND = DexOpcodes.OP_AND;
    public static final int OP_OR = DexOpcodes.OP_OR;
    public static final int OP_XOR = DexOpcodes.OP_XOR;
    public static final int OP_SHL = DexOpcodes.OP_SHL;
    public static final int OP_SHR = DexOpcodes.OP_SHR;
    public static final int OP_USHR = DexOpcodes.OP_USHR;
    public static final int OP_ADD_INT_LIT_X = DexOpcodes.OP_ADD_INT_LIT_X;
    public static final int OP_RSUB_INT_LIT_X = DexOpcodes.OP_RSUB_INT_LIT_X;
    public static final int OP_MUL_INT_LIT_X = DexOpcodes.OP_MUL_INT_LIT_X;
    public static final int OP_DIV_INT_LIT_X = DexOpcodes.OP_DIV_INT_LIT_X;
    public static final int OP_REM_INT_LIT_X = DexOpcodes.OP_REM_INT_LIT_X;
    public static final int OP_AND_INT_LIT_X = DexOpcodes.OP_AND_INT_LIT_X;
    public static final int OP_OR_INT_LIT_X = DexOpcodes.OP_OR_INT_LIT_X;
    public static final int OP_XOR_INT_LIT_X = DexOpcodes.OP_XOR_INT_LIT_X;
    public static final int OP_SHL_INT_LIT_X = DexOpcodes.OP_SHL_INT_LIT_X;
    public static final int OP_SHR_INT_LIT_X = DexOpcodes.OP_SHR_INT_LIT_X;
    public static final int OP_USHR_INT_LIT_X = DexOpcodes.OP_USHR_INT_LIT_X;


    @Override
    public int getOpcodeGroup(int opcode) {
        switch (opcode) {
            case DexOpcodes.OP_NOP:
                return OP_IGNORE;
            case DexOpcodes.OP_MOVE:
                return OP_OTHER;
            case DexOpcodes.OP_MOVE_RESULT:
                return OP_OTHER;
            case DexOpcodes.OP_MOVE_EXCEPTION:
                return OP_OTHER;
            case DexOpcodes.OP_RETURN_VOID:
                return OP_OTHER;
            case DexOpcodes.OP_RETURN:
                return OP_OTHER;
            case DexOpcodes.OP_CONST:
                return OP_OTHER;
            case DexOpcodes.OP_CONST_STRING:
                return OP_OTHER;
            case DexOpcodes.OP_CONST_CLASS:
                return OP_OTHER;
            case DexOpcodes.OP_MONITOR_ENTER:
                return OP_OTHER;
            case DexOpcodes.OP_MONITOR_EXIT:
                return OP_OTHER;
            case DexOpcodes.OP_CHECK_CAST:
                return OP_OTHER;
            case DexOpcodes.OP_INSTANCE_OF:
                return OP_OTHER;
            case DexOpcodes.OP_ARRAY_LENGTH:
                return OP_OTHER;
            case DexOpcodes.OP_NEW_INSTANCE:
                return OP_OTHER;
            case DexOpcodes.OP_NEW_ARRAY:
                return OP_OTHER;
            case DexOpcodes.OP_FILLED_NEW_ARRAY:
                return OP_OTHER;
            case DexOpcodes.OP_FILL_ARRAY_DATA:
                return OP_OTHER;
            case DexOpcodes.OP_THROW:
                return OP_OTHER;
            case DexOpcodes.OP_GOTO:
                return OP_OTHER;
            case DexOpcodes.OP_PACKED_SWITCH:
                return OP_OTHER;
            case DexOpcodes.OP_SPARSE_SWITCH:
                return OP_OTHER;
            case DexOpcodes.OP_CMPL:
                return OP_OTHER;
            case DexOpcodes.OP_CMPG:
                return OP_OTHER;
            case DexOpcodes.OP_CMP:
                return OP_OTHER;
            case DexOpcodes.OP_IF_EQ:
                return OP_OTHER;
            case DexOpcodes.OP_IF_NE:
                return OP_OTHER;
            case DexOpcodes.OP_IF_LT:
                return OP_OTHER;
            case DexOpcodes.OP_IF_GE:
                return OP_OTHER;
            case DexOpcodes.OP_IF_GT:
                return OP_OTHER;
            case DexOpcodes.OP_IF_LE:
                return OP_OTHER;
            case DexOpcodes.OP_IF_EQZ:
                return OP_OTHER;
            case DexOpcodes.OP_IF_NEZ:
                return OP_OTHER;
            case DexOpcodes.OP_IF_LTZ:
                return OP_OTHER;
            case DexOpcodes.OP_IF_GEZ:
                return OP_OTHER;
            case DexOpcodes.OP_IF_GTZ:
                return OP_OTHER;
            case DexOpcodes.OP_IF_LEZ:
                return OP_OTHER;
            case DexOpcodes.OP_AGET:
                return OP_OTHER;
            case DexOpcodes.OP_APUT:
                return OP_OTHER;
            case DexOpcodes.OP_IGET:
                return OP_OTHER;
            case DexOpcodes.OP_IPUT:
                return OP_OTHER;
            case DexOpcodes.OP_SGET:
                return OP_OTHER;
            case DexOpcodes.OP_SPUT:
                return OP_OTHER;
            case DexOpcodes.OP_INVOKE_VIRTUAL:
                return OP_OTHER;
            case DexOpcodes.OP_INVOKE_SUPER:
                return OP_OTHER;
            case DexOpcodes.OP_INVOKE_DIRECT:
                return OP_OTHER;
            case DexOpcodes.OP_INVOKE_STATIC:
                return OP_OTHER;
            case DexOpcodes.OP_INVOKE_INTERFACE:
                return OP_OTHER;
            case DexOpcodes.OP_NEG:
                return OP_NEG;
            case DexOpcodes.OP_NOT:
                return OP_NOT;
            case DexOpcodes.OP_X_TO_Y:
                return OP_X_TO_Y;
            case DexOpcodes.OP_ADD:
                return OP_ADD;
            case DexOpcodes.OP_SUB:
                return OP_SUB;
            case DexOpcodes.OP_MUL:
                return OP_MUL;
            case DexOpcodes.OP_DIV:
                return OP_DIV;
            case DexOpcodes.OP_REM:
                return OP_REM;
            case DexOpcodes.OP_AND:
                return OP_AND;
            case DexOpcodes.OP_OR:
                return OP_OR;
            case DexOpcodes.OP_XOR:
                return OP_XOR;
            case DexOpcodes.OP_SHL:
                return OP_SHL;
            case DexOpcodes.OP_SHR:
                return OP_SHR;
            case DexOpcodes.OP_USHR:
                return OP_USHR;
            case DexOpcodes.OP_ADD_INT_LIT_X:
                return OP_ADD_INT_LIT_X;
            case DexOpcodes.OP_RSUB_INT_LIT_X:
                return OP_RSUB_INT_LIT_X;
            case DexOpcodes.OP_MUL_INT_LIT_X:
                return OP_MUL_INT_LIT_X;
            case DexOpcodes.OP_DIV_INT_LIT_X:
                return OP_DIV_INT_LIT_X;
            case DexOpcodes.OP_REM_INT_LIT_X:
                return OP_REM_INT_LIT_X;
            case DexOpcodes.OP_AND_INT_LIT_X:
                return OP_AND_INT_LIT_X;
            case DexOpcodes.OP_OR_INT_LIT_X:
                return OP_OR_INT_LIT_X;
            case DexOpcodes.OP_XOR_INT_LIT_X:
                return OP_XOR_INT_LIT_X;
            case DexOpcodes.OP_SHL_INT_LIT_X:
                return OP_SHL_INT_LIT_X;
            case DexOpcodes.OP_SHR_INT_LIT_X:
                return OP_SHR_INT_LIT_X;
            case DexOpcodes.OP_USHR_INT_LIT_X:
                return OP_USHR_INT_LIT_X;

            // ODEX:
            case OdexOpcodes.OP_THROW_VERIFICATION_ERROR:
                return OP_OTHER;
            case OdexOpcodes.OP_EXECUTE_INLINE:
                return OP_OTHER;
            case OdexOpcodes.OP_INVOKE_SUPER_QUICK:
                return OP_OTHER;
            case OdexOpcodes.OP_INVOKE_VIRTUAL_QUICK:
                return OP_OTHER;
            case OdexOpcodes.OP_IGET_QUICK:
                return OP_OTHER;
            case OdexOpcodes.OP_IPUT_QUICK:
                return OP_OTHER;
        }
        return OP_OTHER;
    }

}
