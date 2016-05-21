import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

// http://iwsttty.hatenablog.com/entry/2014/03/24/011743
public class Permutation<T> {
    private int baseIndex;
    private int index;
    private T[] objs;
    private Boolean isPrev = null;

    private Permutation<T> subPermutation;

    public Permutation(T[] objs) {
        this(0, 0, objs.clone());
    }

    private Permutation(int baseIndex, int index, T[] objs) {
        if (objs == null || objs.length == 0) {
            throw new IllegalArgumentException();
        }

        this.baseIndex = baseIndex;
        this.index = index;
        this.objs = objs;

        if (this.index < this.objs.length - 1) {
            this.subPermutation = new Permutation<T>(this.baseIndex + 1, this.index + 1, this.objs);
        }
    }

    public boolean hasNext() {
        if (this.subPermutation == null) {
            return false;
        }

        boolean result = this.subPermutation.hasNext();
        if (result) {
            return true;
        }

        this.swap(this.baseIndex, this.index);

        ++this.index;
        if (this.objs.length <= this.index) {
            this.index = this.baseIndex;
            return false;
        }

        this.swap(this.index, this.baseIndex);
        return true;
    }

    private void swap(int index1, int index2) {
        T tmp = this.objs[index1];
        this.objs[index1] = this.objs[index2];
        this.objs[index2] = tmp;
    }

    public List<T> next() {
        return new ArrayList<>(Arrays.asList(this.objs));
    }
}