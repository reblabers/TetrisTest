package core;

import com.sun.istack.internal.NotNull;
import pentmino.enums.PentminoType;
import pentmino.enums.RotateState;
import pentmino.obj.Coordinate;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

public class Field {
    public static final int FIELD_TOP = 19;
    private static final Base64.Encoder ENCODER = Base64.getEncoder();

    @NotNull
    private long[] board = new long[5]; // 0: 0-4, 4: 15-19, 5: 20-24

    public Field() {
    }

    public Field(Field field) {
        this.board = Arrays.copyOf(field.board, field.board.length);
    }

    /**
     * 指定した座標にブロックがないか確認
     *
     * @param coordinate チェックする座標
     * @return ブロックがないとき{@code true}
     */
    public boolean isEmpty(Coordinate coordinate) {
        return isEmpty(coordinate.x, coordinate.y);
    }

    public boolean isEmpty(int x, int y) {
        if (!isInField(x, y))
            return false;

        int boardNumber = FieldUtil.getBoardNumber(y);
        long mask = FieldUtil.getBlockMask(x, y);
        long value = board[boardNumber] & mask;
        return value == 0L;
    }

    public boolean isInField(Coordinate coordinate) {
        return isInField(coordinate.x, coordinate.y);
    }

    public boolean isInField(int x, int y) {
        return FieldUtil.isInField(x, y);
    }

    /**
     * 指定したY座標が0となる部分的なフィールドを取得
     *
     * @param y      部分的なフィールドで最も下となるY座標
     * @param height 部分的なフィールドの高さ
     * @return 部分的なフィールドを表す数値
     */
    public long subField(int y, int height) {
        assert 0 <= height && height <= 6;

        int boardNumber = FieldUtil.getBoardNumber(y);

        int newY = y + height;
        int bitPosition2 = FieldUtil.getBitPosition(newY);
        int boardNumber2 = FieldUtil.getBoardNumber(newY);
        if (boardNumber == boardNumber2 || (boardNumber == boardNumber2 - 1 && bitPosition2 == 0)) {
            return extractLine(board, y, height);
        } else if (boardNumber == boardNumber2 - 1 || (boardNumber == boardNumber2 - 2 && bitPosition2 == 0)) {
            int slide = FieldUtil.getLineSlide(y);
            long left = board[boardNumber] >> slide;
            long mask = FieldUtil.getMultiLineMask(bitPosition2);
            long right = board[boardNumber2] & mask;
            int leftDigit = FieldUtil.getDigit(slide);
            return (right << leftDigit) + left;
        } else {
            assert false;
            throw new UnsupportedOperationException();
        }
    }

    private long extractLine(long[] board, int y, int height) {
        int slide = FieldUtil.getLineSlide(y);
        int boardNumber = FieldUtil.getBoardNumber(y);
        return extractLine(board[boardNumber], slide, height);
    }

    private long extractLine(long board, int slide, int height) {
        long mask = FieldUtil.getMultiLineMask(height);
        return (board >> slide) & mask;
    }

    public void put(Coordinate coordinate) {
        put(coordinate.x, coordinate.y);
    }

    public void put(int x, int y) {
        int boardNumber = FieldUtil.getBoardNumber(y);
        board[boardNumber] |= FieldUtil.getBlockMask(x, y);
    }

    public void show() {
        for (int y = FIELD_TOP; 0 <= y; y--) {
            long line = extractLine(board, y, 1);
            String str = parseLineToString(line);
            System.out.println(str);
        }
    }

    private String parseLineToString(long line) {
        String str = "0000000000" + Long.toBinaryString(line);
        int length = str.length();
        return String.format("%10s", str.substring(length - FieldUtil.FIELD_WIDTH, length));
    }

    /**
     * フィールドからラインを消去し、消去したライン数を返却
     *
     * @return 消去ライン数
     */
    public int clearLine() {
        long lineMask = 0x3FF;
        int newLineNumber = 0;
        int deletedLine = 0;
        long[] newBoard = new long[5];
        for (int y = 0; y < FIELD_TOP; y++) {
            long line = extractLine(board, y, 1);
            if ((line ^ lineMask) != 0L) {
                int slide = FieldUtil.getLineSlide(newLineNumber);
                int newBoardNumber = FieldUtil.getBoardNumber(newLineNumber);
                newBoard[newBoardNumber] = newBoard[newBoardNumber] | (line << slide);
                newLineNumber++;
            } else {
                deletedLine++;
            }
        }
        this.board = newBoard;
        return deletedLine;
    }

    public String toHashString() {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES * board.length);
        for (long b : board)
            buffer.putLong(b);
        byte[] array = buffer.array();
        return ENCODER.encodeToString(array);
    }

    public int maxHeight() {
        for (int y = FIELD_TOP; 0 <= y; y--) {
            long line = extractLine(board, y, 1);
            if (line != 0L)
                return y + 1;
        }
        return 0;
    }

    public int lowDigCount() {
        int count = 0;
        long upBlockLine = extractLine(board, FIELD_TOP, 1);
        for (int y = FIELD_TOP - 1; 0 <= y; y--) {
            long line = extractLine(board, y, 1);
            long digLine = line ^ 0x3FF;
            while (digLine != 0L) {
                long targetDigCut = ((digLine | (digLine - 1)) + 1) & digLine;
                long targetDig = digLine ^ targetDigCut;
                long lineDiff = (upBlockLine & targetDig) ^ targetDig;
                if (lineDiff == 0L) {
                    count++;
                    break;
                }
                digLine = targetDigCut;
            }
            upBlockLine = line;
        }
        return count;
    }
}