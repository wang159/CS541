/* Generated By:JJTree&JavaCC: Do not edit this line. MiniSqlTokenManager.java */
package parser;

@SuppressWarnings({"unchecked", "unused", "static-access"})
public class MiniSqlTokenManager implements MiniSqlConstants
{
  public  java.io.PrintStream debugStream = System.out;
  public  void setDebugStream(java.io.PrintStream ds) { debugStream = ds; }
private final int jjStopStringLiteralDfa_0(int pos, long active0)
{
   switch (pos)
   {
      case 0:
         if ((active0 & 0x3ffffffc0L) != 0L)
         {
            jjmatchedKind = 48;
            return 25;
         }
         return -1;
      case 1:
         if ((active0 & 0x3fe3fff40L) != 0L)
         {
            if (jjmatchedPos != 1)
            {
               jjmatchedKind = 48;
               jjmatchedPos = 1;
            }
            return 25;
         }
         if ((active0 & 0x1c00080L) != 0L)
            return 25;
         return -1;
      case 2:
         if ((active0 & 0x3f73fff00L) != 0L)
         {
            jjmatchedKind = 48;
            jjmatchedPos = 2;
            return 25;
         }
         if ((active0 & 0x8000040L) != 0L)
            return 25;
         return -1;
      case 3:
         if ((active0 & 0x3f51d5300L) != 0L)
         {
            if (jjmatchedPos != 3)
            {
               jjmatchedKind = 48;
               jjmatchedPos = 3;
            }
            return 25;
         }
         if ((active0 & 0x222ac00L) != 0L)
            return 25;
         return -1;
      case 4:
         if ((active0 & 0x194185b00L) != 0L)
         {
            jjmatchedKind = 48;
            jjmatchedPos = 4;
            return 25;
         }
         if ((active0 & 0x261050000L) != 0L)
            return 25;
         return -1;
      case 5:
         if ((active0 & 0x105800L) != 0L)
         {
            jjmatchedKind = 48;
            jjmatchedPos = 5;
            return 25;
         }
         if ((active0 & 0x194080300L) != 0L)
            return 25;
         return -1;
      case 6:
         if ((active0 & 0x1800L) != 0L)
         {
            jjmatchedKind = 48;
            jjmatchedPos = 6;
            return 25;
         }
         if ((active0 & 0x104000L) != 0L)
            return 25;
         return -1;
      default :
         return -1;
   }
}
private final int jjStartNfa_0(int pos, long active0)
{
   return jjMoveNfa_0(jjStopStringLiteralDfa_0(pos, active0), pos + 1);
}
private final int jjStopAtPos(int pos, int kind)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   return pos + 1;
}
private final int jjStartNfaWithStates_0(int pos, int kind, int state)
{
   jjmatchedKind = kind;
   jjmatchedPos = pos;
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) { return pos + 1; }
   return jjMoveNfa_0(state, pos + 1);
}
private final int jjMoveStringLiteralDfa0_0()
{
   switch(curChar)
   {
      case 40:
         return jjStopAtPos(0, 40);
      case 41:
         return jjStopAtPos(0, 41);
      case 42:
         return jjStopAtPos(0, 44);
      case 44:
         return jjStopAtPos(0, 42);
      case 59:
         return jjStopAtPos(0, 43);
      case 60:
         jjmatchedKind = 38;
         return jjMoveStringLiteralDfa1_0(0x8800000000L);
      case 61:
         return jjStopAtPos(0, 34);
      case 62:
         jjmatchedKind = 36;
         return jjMoveStringLiteralDfa1_0(0x2000000000L);
      case 65:
      case 97:
         return jjMoveStringLiteralDfa1_0(0x40L);
      case 66:
      case 98:
         return jjMoveStringLiteralDfa1_0(0x80L);
      case 67:
      case 99:
         return jjMoveStringLiteralDfa1_0(0x100L);
      case 68:
      case 100:
         return jjMoveStringLiteralDfa1_0(0x3e00L);
      case 69:
      case 101:
         return jjMoveStringLiteralDfa1_0(0x4000L);
      case 70:
      case 102:
         return jjMoveStringLiteralDfa1_0(0x30000L);
      case 72:
      case 104:
         return jjMoveStringLiteralDfa1_0(0x8000L);
      case 73:
      case 105:
         return jjMoveStringLiteralDfa1_0(0x3c0000L);
      case 79:
      case 111:
         return jjMoveStringLiteralDfa1_0(0x1c00000L);
      case 81:
      case 113:
         return jjMoveStringLiteralDfa1_0(0x2000000L);
      case 83:
      case 115:
         return jjMoveStringLiteralDfa1_0(0x3c000000L);
      case 84:
      case 116:
         return jjMoveStringLiteralDfa1_0(0x40000000L);
      case 85:
      case 117:
         return jjMoveStringLiteralDfa1_0(0x80000000L);
      case 86:
      case 118:
         return jjMoveStringLiteralDfa1_0(0x100000000L);
      case 87:
      case 119:
         return jjMoveStringLiteralDfa1_0(0x200000000L);
      default :
         return jjMoveNfa_0(5, 0);
   }
}
private final int jjMoveStringLiteralDfa1_0(long active0)
{
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(0, active0);
      return 1;
   }
   switch(curChar)
   {
      case 61:
         if ((active0 & 0x2000000000L) != 0L)
            return jjStopAtPos(1, 37);
         else if ((active0 & 0x8000000000L) != 0L)
            return jjStopAtPos(1, 39);
         break;
      case 62:
         if ((active0 & 0x800000000L) != 0L)
            return jjStopAtPos(1, 35);
         break;
      case 65:
      case 97:
         return jjMoveStringLiteralDfa2_0(active0, 0x140000000L);
      case 69:
      case 101:
         return jjMoveStringLiteralDfa2_0(active0, 0xc008e00L);
      case 72:
      case 104:
         return jjMoveStringLiteralDfa2_0(active0, 0x200000000L);
      case 73:
      case 105:
         return jjMoveStringLiteralDfa2_0(active0, 0x1000L);
      case 76:
      case 108:
         return jjMoveStringLiteralDfa2_0(active0, 0x10000L);
      case 78:
      case 110:
         if ((active0 & 0x400000L) != 0L)
            return jjStartNfaWithStates_0(1, 22, 25);
         return jjMoveStringLiteralDfa2_0(active0, 0x3c0040L);
      case 80:
      case 112:
         return jjMoveStringLiteralDfa2_0(active0, 0x80000000L);
      case 82:
      case 114:
         if ((active0 & 0x800000L) != 0L)
         {
            jjmatchedKind = 23;
            jjmatchedPos = 1;
         }
         return jjMoveStringLiteralDfa2_0(active0, 0x1022100L);
      case 84:
      case 116:
         return jjMoveStringLiteralDfa2_0(active0, 0x30000000L);
      case 85:
      case 117:
         return jjMoveStringLiteralDfa2_0(active0, 0x2000000L);
      case 88:
      case 120:
         return jjMoveStringLiteralDfa2_0(active0, 0x4000L);
      case 89:
      case 121:
         if ((active0 & 0x80L) != 0L)
            return jjStartNfaWithStates_0(1, 7, 25);
         break;
      default :
         break;
   }
   return jjStartNfa_0(0, active0);
}
private final int jjMoveStringLiteralDfa2_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(0, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(1, active0);
      return 2;
   }
   switch(curChar)
   {
      case 65:
      case 97:
         return jjMoveStringLiteralDfa3_0(active0, 0x20000000L);
      case 66:
      case 98:
         return jjMoveStringLiteralDfa3_0(active0, 0x40000000L);
      case 68:
      case 100:
         if ((active0 & 0x40L) != 0L)
            return jjStartNfaWithStates_0(2, 6, 25);
         return jjMoveStringLiteralDfa3_0(active0, 0x81040000L);
      case 69:
      case 101:
         return jjMoveStringLiteralDfa3_0(active0, 0x200000100L);
      case 73:
      case 105:
         return jjMoveStringLiteralDfa3_0(active0, 0x2000000L);
      case 76:
      case 108:
         return jjMoveStringLiteralDfa3_0(active0, 0x104008200L);
      case 79:
      case 111:
         return jjMoveStringLiteralDfa3_0(active0, 0x32000L);
      case 80:
      case 112:
         return jjMoveStringLiteralDfa3_0(active0, 0x4000L);
      case 82:
      case 114:
         return jjMoveStringLiteralDfa3_0(active0, 0x10000000L);
      case 83:
      case 115:
         return jjMoveStringLiteralDfa3_0(active0, 0x81c00L);
      case 84:
      case 116:
         if ((active0 & 0x8000000L) != 0L)
            return jjStartNfaWithStates_0(2, 27, 25);
         return jjMoveStringLiteralDfa3_0(active0, 0x300000L);
      default :
         break;
   }
   return jjStartNfa_0(1, active0);
}
private final int jjMoveStringLiteralDfa3_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(1, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(2, active0);
      return 3;
   }
   switch(curChar)
   {
      case 65:
      case 97:
         return jjMoveStringLiteralDfa4_0(active0, 0x80010100L);
      case 67:
      case 99:
         if ((active0 & 0x400L) != 0L)
         {
            jjmatchedKind = 10;
            jjmatchedPos = 3;
         }
         return jjMoveStringLiteralDfa4_0(active0, 0x800L);
      case 69:
      case 101:
         return jjMoveStringLiteralDfa4_0(active0, 0x51c0200L);
      case 73:
      case 105:
         return jjMoveStringLiteralDfa4_0(active0, 0x10000000L);
      case 76:
      case 108:
         return jjMoveStringLiteralDfa4_0(active0, 0x40004000L);
      case 77:
      case 109:
         if ((active0 & 0x20000L) != 0L)
            return jjStartNfaWithStates_0(3, 17, 25);
         break;
      case 79:
      case 111:
         if ((active0 & 0x200000L) != 0L)
            return jjStartNfaWithStates_0(3, 21, 25);
         break;
      case 80:
      case 112:
         if ((active0 & 0x2000L) != 0L)
            return jjStartNfaWithStates_0(3, 13, 25);
         else if ((active0 & 0x8000L) != 0L)
            return jjStartNfaWithStates_0(3, 15, 25);
         break;
      case 82:
      case 114:
         return jjMoveStringLiteralDfa4_0(active0, 0x200000000L);
      case 84:
      case 116:
         if ((active0 & 0x2000000L) != 0L)
            return jjStartNfaWithStates_0(3, 25, 25);
         return jjMoveStringLiteralDfa4_0(active0, 0x20001000L);
      case 85:
      case 117:
         return jjMoveStringLiteralDfa4_0(active0, 0x100000000L);
      default :
         break;
   }
   return jjStartNfa_0(2, active0);
}
private final int jjMoveStringLiteralDfa4_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(2, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(3, active0);
      return 4;
   }
   switch(curChar)
   {
      case 65:
      case 97:
         return jjMoveStringLiteralDfa5_0(active0, 0x4000L);
      case 67:
      case 99:
         return jjMoveStringLiteralDfa5_0(active0, 0x4000000L);
      case 69:
      case 101:
         if ((active0 & 0x40000000L) != 0L)
            return jjStartNfaWithStates_0(4, 30, 25);
         else if ((active0 & 0x200000000L) != 0L)
            return jjStartNfaWithStates_0(4, 33, 25);
         return jjMoveStringLiteralDfa5_0(active0, 0x100000000L);
      case 71:
      case 103:
         return jjMoveStringLiteralDfa5_0(active0, 0x100000L);
      case 73:
      case 105:
         return jjMoveStringLiteralDfa5_0(active0, 0x1000L);
      case 78:
      case 110:
         return jjMoveStringLiteralDfa5_0(active0, 0x10000000L);
      case 82:
      case 114:
         if ((active0 & 0x1000000L) != 0L)
            return jjStartNfaWithStates_0(4, 24, 25);
         return jjMoveStringLiteralDfa5_0(active0, 0x80800L);
      case 83:
      case 115:
         if ((active0 & 0x20000000L) != 0L)
            return jjStartNfaWithStates_0(4, 29, 25);
         break;
      case 84:
      case 116:
         if ((active0 & 0x10000L) != 0L)
            return jjStartNfaWithStates_0(4, 16, 25);
         return jjMoveStringLiteralDfa5_0(active0, 0x80000300L);
      case 88:
      case 120:
         if ((active0 & 0x40000L) != 0L)
            return jjStartNfaWithStates_0(4, 18, 25);
         break;
      default :
         break;
   }
   return jjStartNfa_0(3, active0);
}
private final int jjMoveStringLiteralDfa5_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(3, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(4, active0);
      return 5;
   }
   switch(curChar)
   {
      case 69:
      case 101:
         if ((active0 & 0x100L) != 0L)
            return jjStartNfaWithStates_0(5, 8, 25);
         else if ((active0 & 0x200L) != 0L)
            return jjStartNfaWithStates_0(5, 9, 25);
         else if ((active0 & 0x80000000L) != 0L)
            return jjStartNfaWithStates_0(5, 31, 25);
         return jjMoveStringLiteralDfa6_0(active0, 0x100000L);
      case 71:
      case 103:
         if ((active0 & 0x10000000L) != 0L)
            return jjStartNfaWithStates_0(5, 28, 25);
         break;
      case 73:
      case 105:
         return jjMoveStringLiteralDfa6_0(active0, 0x4800L);
      case 78:
      case 110:
         return jjMoveStringLiteralDfa6_0(active0, 0x1000L);
      case 83:
      case 115:
         if ((active0 & 0x100000000L) != 0L)
            return jjStartNfaWithStates_0(5, 32, 25);
         break;
      case 84:
      case 116:
         if ((active0 & 0x80000L) != 0L)
            return jjStartNfaWithStates_0(5, 19, 25);
         else if ((active0 & 0x4000000L) != 0L)
            return jjStartNfaWithStates_0(5, 26, 25);
         break;
      default :
         break;
   }
   return jjStartNfa_0(4, active0);
}
private final int jjMoveStringLiteralDfa6_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(4, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(5, active0);
      return 6;
   }
   switch(curChar)
   {
      case 66:
      case 98:
         return jjMoveStringLiteralDfa7_0(active0, 0x800L);
      case 67:
      case 99:
         return jjMoveStringLiteralDfa7_0(active0, 0x1000L);
      case 78:
      case 110:
         if ((active0 & 0x4000L) != 0L)
            return jjStartNfaWithStates_0(6, 14, 25);
         break;
      case 82:
      case 114:
         if ((active0 & 0x100000L) != 0L)
            return jjStartNfaWithStates_0(6, 20, 25);
         break;
      default :
         break;
   }
   return jjStartNfa_0(5, active0);
}
private final int jjMoveStringLiteralDfa7_0(long old0, long active0)
{
   if (((active0 &= old0)) == 0L)
      return jjStartNfa_0(5, old0); 
   try { curChar = input_stream.readChar(); }
   catch(java.io.IOException e) {
      jjStopStringLiteralDfa_0(6, active0);
      return 7;
   }
   switch(curChar)
   {
      case 69:
      case 101:
         if ((active0 & 0x800L) != 0L)
            return jjStartNfaWithStates_0(7, 11, 25);
         break;
      case 84:
      case 116:
         if ((active0 & 0x1000L) != 0L)
            return jjStartNfaWithStates_0(7, 12, 25);
         break;
      default :
         break;
   }
   return jjStartNfa_0(6, active0);
}
private final void jjCheckNAdd(int state)
{
   if (jjrounds[state] != jjround)
   {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
   }
}
private final void jjAddStates(int start, int end)
{
   do {
      jjstateSet[jjnewStateCnt++] = jjnextStates[start];
   } while (start++ != end);
}
private final void jjCheckNAddTwoStates(int state1, int state2)
{
   jjCheckNAdd(state1);
   jjCheckNAdd(state2);
}
private final void jjCheckNAddStates(int start, int end)
{
   do {
      jjCheckNAdd(jjnextStates[start]);
   } while (start++ != end);
}
private final void jjCheckNAddStates(int start)
{
   jjCheckNAdd(jjnextStates[start]);
   jjCheckNAdd(jjnextStates[start + 1]);
}
static final long[] jjbitVec0 = {
   0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL
};
private final int jjMoveNfa_0(int startState, int curPos)
{
   int[] nextStates;
   int startsAt = 0;
   jjnewStateCnt = 25;
   int i = 1;
   jjstateSet[0] = startState;
   int j, kind = 0x7fffffff;
   for (;;)
   {
      if (++jjround == 0x7fffffff)
         ReInitRounds();
      if (curChar < 64)
      {
         long l = 1L << curChar;
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 25:
               case 4:
                  if ((0x3ff001000000000L & l) == 0L)
                     break;
                  if (kind > 48)
                     kind = 48;
                  jjCheckNAdd(4);
                  break;
               case 5:
                  if ((0x3ff000000000000L & l) != 0L)
                  {
                     if (kind > 45)
                        kind = 45;
                     jjCheckNAddStates(0, 2);
                  }
                  else if (curChar == 45)
                     jjCheckNAddStates(3, 6);
                  else if (curChar == 47)
                     jjstateSet[jjnewStateCnt++] = 13;
                  else if (curChar == 39)
                     jjCheckNAddTwoStates(6, 7);
                  else if (curChar == 46)
                     jjCheckNAddTwoStates(1, 2);
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 10;
                  break;
               case 0:
                  if (curChar == 46)
                     jjCheckNAddTwoStates(1, 2);
                  break;
               case 1:
                  if (curChar == 45)
                     jjCheckNAdd(2);
                  break;
               case 2:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 47)
                     kind = 47;
                  jjCheckNAdd(2);
                  break;
               case 6:
                  if ((0xffffff7fffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(6, 7);
                  break;
               case 7:
                  if (curChar != 39)
                     break;
                  if (kind > 51)
                     kind = 51;
                  jjstateSet[jjnewStateCnt++] = 8;
                  break;
               case 8:
                  if (curChar == 39)
                     jjCheckNAddTwoStates(9, 7);
                  break;
               case 9:
                  if ((0xffffff7fffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(9, 7);
                  break;
               case 10:
                  if (curChar != 45)
                     break;
                  if (kind > 52)
                     kind = 52;
                  jjCheckNAdd(11);
                  break;
               case 11:
                  if ((0xffffffffffffdbffL & l) == 0L)
                     break;
                  if (kind > 52)
                     kind = 52;
                  jjCheckNAdd(11);
                  break;
               case 12:
                  if (curChar == 45)
                     jjstateSet[jjnewStateCnt++] = 10;
                  break;
               case 13:
                  if (curChar == 42)
                     jjCheckNAddTwoStates(14, 15);
                  break;
               case 14:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(14, 15);
                  break;
               case 15:
                  if (curChar == 42)
                     jjCheckNAddStates(7, 9);
                  break;
               case 16:
                  if ((0xffff7bffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(17, 15);
                  break;
               case 17:
                  if ((0xfffffbffffffffffL & l) != 0L)
                     jjCheckNAddTwoStates(17, 15);
                  break;
               case 18:
                  if (curChar == 47 && kind > 53)
                     kind = 53;
                  break;
               case 19:
                  if (curChar == 47)
                     jjstateSet[jjnewStateCnt++] = 13;
                  break;
               case 20:
                  if (curChar == 45)
                     jjCheckNAddStates(3, 6);
                  break;
               case 21:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 45)
                     kind = 45;
                  jjCheckNAdd(21);
                  break;
               case 22:
                  if (curChar == 45)
                     jjCheckNAdd(23);
                  break;
               case 23:
                  if ((0x3ff000000000000L & l) != 0L)
                     jjCheckNAddTwoStates(23, 0);
                  break;
               case 24:
                  if ((0x3ff000000000000L & l) == 0L)
                     break;
                  if (kind > 45)
                     kind = 45;
                  jjCheckNAddStates(0, 2);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else if (curChar < 128)
      {
         long l = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 25:
                  if ((0x7fffffe87fffffeL & l) != 0L)
                  {
                     if (kind > 48)
                        kind = 48;
                     jjCheckNAdd(4);
                  }
                  if ((0x7fffffe07fffffeL & l) != 0L)
                  {
                     if (kind > 48)
                        kind = 48;
                     jjCheckNAddTwoStates(3, 4);
                  }
                  break;
               case 5:
               case 3:
                  if ((0x7fffffe07fffffeL & l) == 0L)
                     break;
                  if (kind > 48)
                     kind = 48;
                  jjCheckNAddTwoStates(3, 4);
                  break;
               case 4:
                  if ((0x7fffffe87fffffeL & l) == 0L)
                     break;
                  if (kind > 48)
                     kind = 48;
                  jjCheckNAdd(4);
                  break;
               case 6:
                  jjCheckNAddTwoStates(6, 7);
                  break;
               case 9:
                  jjCheckNAddTwoStates(9, 7);
                  break;
               case 11:
                  if (kind > 52)
                     kind = 52;
                  jjstateSet[jjnewStateCnt++] = 11;
                  break;
               case 14:
                  jjCheckNAddTwoStates(14, 15);
                  break;
               case 16:
               case 17:
                  jjCheckNAddTwoStates(17, 15);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      else
      {
         int i2 = (curChar & 0xff) >> 6;
         long l2 = 1L << (curChar & 077);
         MatchLoop: do
         {
            switch(jjstateSet[--i])
            {
               case 6:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(6, 7);
                  break;
               case 9:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(9, 7);
                  break;
               case 11:
                  if ((jjbitVec0[i2] & l2) == 0L)
                     break;
                  if (kind > 52)
                     kind = 52;
                  jjstateSet[jjnewStateCnt++] = 11;
                  break;
               case 14:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(14, 15);
                  break;
               case 16:
               case 17:
                  if ((jjbitVec0[i2] & l2) != 0L)
                     jjCheckNAddTwoStates(17, 15);
                  break;
               default : break;
            }
         } while(i != startsAt);
      }
      if (kind != 0x7fffffff)
      {
         jjmatchedKind = kind;
         jjmatchedPos = curPos;
         kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 25 - (jjnewStateCnt = startsAt)))
         return curPos;
      try { curChar = input_stream.readChar(); }
      catch(java.io.IOException e) { return curPos; }
   }
}
static final int[] jjnextStates = {
   21, 23, 0, 21, 22, 23, 0, 15, 16, 18, 
};
public static final String[] jjstrLiteralImages = {
"", null, null, null, null, null, null, null, null, null, null, null, null, 
null, null, null, null, null, null, null, null, null, null, null, null, null, null, 
null, null, null, null, null, null, null, "\75", "\74\76", "\76", "\76\75", "\74", 
"\74\75", "\50", "\51", "\54", "\73", "\52", null, null, null, null, null, null, null, 
null, null, };
public static final String[] lexStateNames = {
   "DEFAULT", 
};
static final long[] jjtoToken = {
   0x9bfffffffffc1L, 
};
static final long[] jjtoSkip = {
   0x3000000000003eL, 
};
static final long[] jjtoSpecial = {
   0x30000000000000L, 
};
protected SimpleCharStream input_stream;
private final int[] jjrounds = new int[25];
private final int[] jjstateSet = new int[50];
protected char curChar;
public MiniSqlTokenManager(SimpleCharStream stream){
   if (SimpleCharStream.staticFlag)
      throw new Error("ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
   input_stream = stream;
}
public MiniSqlTokenManager(SimpleCharStream stream, int lexState){
   this(stream);
   SwitchTo(lexState);
}
public void ReInit(SimpleCharStream stream)
{
   jjmatchedPos = jjnewStateCnt = 0;
   curLexState = defaultLexState;
   input_stream = stream;
   ReInitRounds();
}
private final void ReInitRounds()
{
   int i;
   jjround = 0x80000001;
   for (i = 25; i-- > 0;)
      jjrounds[i] = 0x80000000;
}
public void ReInit(SimpleCharStream stream, int lexState)
{
   ReInit(stream);
   SwitchTo(lexState);
}
public void SwitchTo(int lexState)
{
   if (lexState >= 1 || lexState < 0)
      throw new TokenMgrError("Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.", TokenMgrError.INVALID_LEXICAL_STATE);
   else
      curLexState = lexState;
}

protected Token jjFillToken()
{
   Token t = Token.newToken(jjmatchedKind);
   t.kind = jjmatchedKind;
   String im = jjstrLiteralImages[jjmatchedKind];
   t.image = (im == null) ? input_stream.GetImage() : im;
   t.beginLine = input_stream.getBeginLine();
   t.beginColumn = input_stream.getBeginColumn();
   t.endLine = input_stream.getEndLine();
   t.endColumn = input_stream.getEndColumn();
   return t;
}

int curLexState = 0;
int defaultLexState = 0;
int jjnewStateCnt;
int jjround;
int jjmatchedPos;
int jjmatchedKind;

public Token getNextToken() 
{
  int kind;
  Token specialToken = null;
  Token matchedToken;
  int curPos = 0;

  EOFLoop :
  for (;;)
  {   
   try   
   {     
      curChar = input_stream.BeginToken();
   }     
   catch(java.io.IOException e)
   {        
      jjmatchedKind = 0;
      matchedToken = jjFillToken();
      matchedToken.specialToken = specialToken;
      return matchedToken;
   }

   try { input_stream.backup(0);
      while (curChar <= 32 && (0x100003600L & (1L << curChar)) != 0L)
         curChar = input_stream.BeginToken();
   }
   catch (java.io.IOException e1) { continue EOFLoop; }
   jjmatchedKind = 0x7fffffff;
   jjmatchedPos = 0;
   curPos = jjMoveStringLiteralDfa0_0();
   if (jjmatchedKind != 0x7fffffff)
   {
      if (jjmatchedPos + 1 < curPos)
         input_stream.backup(curPos - jjmatchedPos - 1);
      if ((jjtoToken[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
      {
         matchedToken = jjFillToken();
         matchedToken.specialToken = specialToken;
         return matchedToken;
      }
      else
      {
         if ((jjtoSpecial[jjmatchedKind >> 6] & (1L << (jjmatchedKind & 077))) != 0L)
         {
            matchedToken = jjFillToken();
            if (specialToken == null)
               specialToken = matchedToken;
            else
            {
               matchedToken.specialToken = specialToken;
               specialToken = (specialToken.next = matchedToken);
            }
         }
         continue EOFLoop;
      }
   }
   int error_line = input_stream.getEndLine();
   int error_column = input_stream.getEndColumn();
   String error_after = null;
   boolean EOFSeen = false;
   try { input_stream.readChar(); input_stream.backup(1); }
   catch (java.io.IOException e1) {
      EOFSeen = true;
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
      if (curChar == '\n' || curChar == '\r') {
         error_line++;
         error_column = 0;
      }
      else
         error_column++;
   }
   if (!EOFSeen) {
      input_stream.backup(1);
      error_after = curPos <= 1 ? "" : input_stream.GetImage();
   }
   throw new TokenMgrError(EOFSeen, curLexState, error_line, error_column, error_after, curChar, TokenMgrError.LEXICAL_ERROR);
  }
}

}
