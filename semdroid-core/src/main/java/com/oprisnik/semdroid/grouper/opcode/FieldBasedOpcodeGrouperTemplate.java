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
 * Opcode grouper template.
 */
public class FieldBasedOpcodeGrouperTemplate extends FieldBasedOpcodeGrouper {

    // Define your opcode groups here...
    // e.g. :
    public static final int OP_IF = DexOpcodes.OP_IF_EQ;

    @Override
    public int getOpcodeGroup(int opcode) {
        // OP_IGNORE can be used to ignore the opcode
        // return your opcode groups defined above.
        switch (opcode) {
            case DexOpcodes.OP_NOP:
                return 0;
            case DexOpcodes.OP_MOVE:
                return 0;
            case DexOpcodes.OP_MOVE_RESULT:
                return 0;
            case DexOpcodes.OP_MOVE_EXCEPTION:
                return 0;
            case DexOpcodes.OP_RETURN_VOID:
                return 0;
            case DexOpcodes.OP_RETURN:
                return 0;
            case DexOpcodes.OP_CONST:
                return 0;
            case DexOpcodes.OP_CONST_STRING:
                return 0;
            case DexOpcodes.OP_CONST_CLASS:
                return 0;
            case DexOpcodes.OP_MONITOR_ENTER:
                return 0;
            case DexOpcodes.OP_MONITOR_EXIT:
                return 0;
            case DexOpcodes.OP_CHECK_CAST:
                return 0;
            case DexOpcodes.OP_INSTANCE_OF:
                return 0;
            case DexOpcodes.OP_ARRAY_LENGTH:
                return 0;
            case DexOpcodes.OP_NEW_INSTANCE:
                return 0;
            case DexOpcodes.OP_NEW_ARRAY:
                return 0;
            case DexOpcodes.OP_FILLED_NEW_ARRAY:
                return 0;
            case DexOpcodes.OP_FILL_ARRAY_DATA:
                return 0;
            case DexOpcodes.OP_THROW:
                return 0;
            case DexOpcodes.OP_GOTO:
                return 0;
            case DexOpcodes.OP_PACKED_SWITCH:
                return 0;
            case DexOpcodes.OP_SPARSE_SWITCH:
                return 0;
            case DexOpcodes.OP_CMPL:
                return 0;
            case DexOpcodes.OP_CMPG:
                return 0;
            case DexOpcodes.OP_CMP:
                return 0;
            case DexOpcodes.OP_IF_EQ:
                return 0;
            case DexOpcodes.OP_IF_NE:
                return 0;
            case DexOpcodes.OP_IF_LT:
                return 0;
            case DexOpcodes.OP_IF_GE:
                return 0;
            case DexOpcodes.OP_IF_GT:
                return 0;
            case DexOpcodes.OP_IF_LE:
                return 0;
            case DexOpcodes.OP_IF_EQZ:
                return 0;
            case DexOpcodes.OP_IF_NEZ:
                return 0;
            case DexOpcodes.OP_IF_LTZ:
                return 0;
            case DexOpcodes.OP_IF_GEZ:
                return 0;
            case DexOpcodes.OP_IF_GTZ:
                return 0;
            case DexOpcodes.OP_IF_LEZ:
                return 0;
            case DexOpcodes.OP_AGET:
                return 0;
            case DexOpcodes.OP_APUT:
                return 0;
            case DexOpcodes.OP_IGET:
                return 0;
            case DexOpcodes.OP_IPUT:
                return 0;
            case DexOpcodes.OP_SGET:
                return 0;
            case DexOpcodes.OP_SPUT:
                return 0;
            case DexOpcodes.OP_INVOKE_VIRTUAL:
                return 0;
            case DexOpcodes.OP_INVOKE_SUPER:
                return 0;
            case DexOpcodes.OP_INVOKE_DIRECT:
                return 0;
            case DexOpcodes.OP_INVOKE_STATIC:
                return 0;
            case DexOpcodes.OP_INVOKE_INTERFACE:
                return 0;
            case DexOpcodes.OP_NEG:
                return 0;
            case DexOpcodes.OP_NOT:
                return 0;
            case DexOpcodes.OP_X_TO_Y:
                return 0;
            case DexOpcodes.OP_ADD:
                return 0;
            case DexOpcodes.OP_SUB:
                return 0;
            case DexOpcodes.OP_MUL:
                return 0;
            case DexOpcodes.OP_DIV:
                return 0;
            case DexOpcodes.OP_REM:
                return 0;
            case DexOpcodes.OP_AND:
                return 0;
            case DexOpcodes.OP_OR:
                return 0;
            case DexOpcodes.OP_XOR:
                return 0;
            case DexOpcodes.OP_SHL:
                return 0;
            case DexOpcodes.OP_SHR:
                return 0;
            case DexOpcodes.OP_USHR:
                return 0;
            case DexOpcodes.OP_ADD_INT_LIT_X:
                return 0;
            case DexOpcodes.OP_RSUB_INT_LIT_X:
                return 0;
            case DexOpcodes.OP_MUL_INT_LIT_X:
                return 0;
            case DexOpcodes.OP_DIV_INT_LIT_X:
                return 0;
            case DexOpcodes.OP_REM_INT_LIT_X:
                return 0;
            case DexOpcodes.OP_AND_INT_LIT_X:
                return 0;
            case DexOpcodes.OP_OR_INT_LIT_X:
                return 0;
            case DexOpcodes.OP_XOR_INT_LIT_X:
                return 0;
            case DexOpcodes.OP_SHL_INT_LIT_X:
                return 0;
            case DexOpcodes.OP_SHR_INT_LIT_X:
                return 0;
            case DexOpcodes.OP_USHR_INT_LIT_X:
                return 0;

            // ODEX:
            case OdexOpcodes.OP_THROW_VERIFICATION_ERROR:
                return 0;
            case OdexOpcodes.OP_EXECUTE_INLINE:
                return 0;
            case OdexOpcodes.OP_INVOKE_SUPER_QUICK:
                return 0;
            case OdexOpcodes.OP_INVOKE_VIRTUAL_QUICK:
                return 0;
            case OdexOpcodes.OP_IGET_QUICK:
                return 0;
            case OdexOpcodes.OP_IPUT_QUICK:
                return 0;
        }
        return OP_IGNORE;
    }

}
