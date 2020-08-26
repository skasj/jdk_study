package my;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class MyHashMap<K, V> extends AbstractMap<K, V> implements Map<K, V>, Cloneable, Serializable {

    private static final long serialVersionUID = 8318443613011906048L;

    /** 初始化容量大小 16 */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    /** 最大容量 */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /** 加载因子，用于扩容 */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /** 节点，用于Hash数组与红黑树*/
    static class Node<K,V> implements Entry<K,V>{
        final int hash;
        final K key;
        V value;
        Node<K,V> next;

        public Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

        public final K getKey() {
            return key;
        }

        public final V getValue() {
            return value;
        }

        public final String toString() {
            return key + "=" + value;
        }

        public final int hashcode(){
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        /** 返回的是上一个value的值 */
        public final V setValue(V newValue){
            V oldValue = value;
            value = newValue;
            return oldValue;
        }

        public final boolean equals(Object o){
            if (o == this)
                return true;
            if (o instanceof Map.Entry){
                Entry<?,?> e = (Entry<?,?>) o;
                if (Objects.equals(key, e.getKey()) && Objects.equals(value, e.getValue())){
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * 参考https://www.zhihu.com/question/20733617
     */
    static final int hash(Object key){
        int h;
        return key==null ? 0 : (h = key.hashCode())^(h>>>16);
    }

    /* ---------------- Fields -------------- */
    transient MyHashMap.Node<K,V>[] table;
    /** */
    transient int modCount;
    /** 加载因子，扩容时使用 */
    final float loadFactor;
    /** 下次需要扩展时的容量 threshold = capacity * loadFactor */
    int threshold;
    /**
     * 用来避免频繁调整Hash数组容量和链表红黑树切换
     * The smallest table capacity for which bins may be treeified.
     * (Otherwise the table is resized if too many nodes in a bin.)
     * Should be at least 4 * TREEIFY_THRESHOLD to avoid conflicts
     * between resizing and treeification thresholds.
     */
    static final int MIN_TREEIFY_CAPACITY = 64;

    public MyHashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                                       initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                                       loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }

    public MyHashMap(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }
    public MyHashMap() {
        this.loadFactor = DEFAULT_LOAD_FACTOR; // all other fields defaulted
    }
    public MyHashMap(Map<? extends K, ? extends V> m) {
        this.loadFactor = DEFAULT_LOAD_FACTOR;
        putMapEntries(m, false);
    }

    /**
     * Implements Map.putAll and Map constructor
     *
     * @param m the map
     * @param evict false when initially constructing this map, else
     * true (relayed to method afterNodeInsertion).
     */
    final void putMapEntries(Map<? extends K, ? extends V> m, boolean evict) {
        int s = m.size();
        if (s > 0) {
            if (table == null) { // pre-size
                float ft = ((float)s / loadFactor) + 1.0F;
                int t = ((ft < (float)MAXIMUM_CAPACITY) ?
                        (int)ft : MAXIMUM_CAPACITY);
                if (t > threshold)
                    threshold = tableSizeFor(t);
            }
            else if (s > threshold)
                resize();
            for (Entry<? extends K, ? extends V> e : m.entrySet()) {
                K key = e.getKey();
                V value = e.getValue();
                putVal(hash(key), key, value, false, evict);
            }
        }
    }

    /**
     * The number of key-value mappings contained in this map.
     */
    transient int size;

    /** 链表的最大长度，到达这个长度就需要升级为红黑树 */
    static final int TREEIFY_THRESHOLD = 8;

    final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        MyHashMap.Node<K,V>[] tab; MyHashMap.Node<K,V> p; int n, i;

        if ((tab = table) == null || (n = tab.length) == 0)
            // hash 数组未初始化，需要先初始化，n=hash数组长度
            n = (tab = resize()).length;
        if ((p = tab[i = (n - 1) & hash]) == null)
            // 如果没有碰撞，直接赋值
            tab[i] = newNode(hash, key, value, null);
        else {
            // 碰撞，则加入链表，或者加入红黑树
            MyHashMap.Node<K,V> e; K k;
            if (p.hash == hash &&
                    ((k = p.key) == key || (key != null && key.equals(k))))
                // key值已存在，直接替换value
                e = p;
            else if (p instanceof MyHashMap.TreeNode)
                // 是红黑树，用红黑树的方式putValue
                e = ((MyHashMap.TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                // 添加到链表
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            // 加入前，链表长度就大于等于8，需要升级为红黑树
                            treeifyBin(tab, hash);
                        break;
                    }
                    if (e.hash == hash &&
                            ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }

    void afterNodeInsertion(boolean evict) { }
    void afterNodeAccess(MyHashMap.Node<K,V> p) { }


    // Create a regular (non-tree) node
    MyHashMap.Node<K,V> newNode(int hash, K key, V value, MyHashMap.Node<K,V> next) {
        return new MyHashMap.Node<>(hash, key, value, next);
    }


    /** cap必须是2的幂次，该函数使最高位之后的位数全为1 */
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }

    /**
     * Initializes or doubles table size.  If null, allocates in
     * accord with initial capacity target held in field threshold.
     * Otherwise, because we are using power-of-two expansion, the
     * elements from each bin must either stay at same index, or move
     * with a power of two offset in the new table.
     *
     * @return the table
     */
    final MyHashMap.Node<K,V>[] resize() {
        // 老Hash表
        MyHashMap.Node<K,V>[] oldTab = table;
        // 老Hash长度
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        // 老占用上限
        int oldThr = threshold;
        int newCap, newThr = 0;
        // 调整扩容触发点
        if (oldCap > 0) {
            // 老容量>0
            if (oldCap >= MAXIMUM_CAPACITY) {
                // 老容量大于最大容量，无法继续扩展，设置触发扩容的限制为最大（不受限制），使扩容不在被触发，返回原来的Hash表
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                    oldCap >= DEFAULT_INITIAL_CAPACITY)
                // 还可以扩容一倍，扩容限制翻倍
                newThr = oldThr << 1; // double threshold
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
            // 用上一次的限制量作为新容量
            newCap = oldThr;
        else {               // zero initial threshold signifies using defaults
            // 上一的容量是0，说明正在初始化，使用默认值
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {
            // 新的限制量为0，进行初始化赋值
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                    (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
        MyHashMap.Node<K,V>[] newTab = (MyHashMap.Node<K,V>[])new MyHashMap.Node[newCap];
        table = newTab;
        if (oldTab != null) {
            // 是扩容
            for (int j = 0; j < oldCap; ++j) {
                // 遍历
                MyHashMap.Node<K,V> e;
                if ((e = oldTab[j]) != null) {
                    // 节点非空
                    oldTab[j] = null;
                    if (e.next == null)
                        // 只有一个节点，重新hash即可
                        newTab[e.hash & (newCap - 1)] = e;
                    else if (e instanceof MyHashMap.TreeNode)
                        // 如果是红黑树，需要拆分，长的依旧组装成红黑树，断的直接作为链表
                        ((MyHashMap.TreeNode<K,V>)e).split(this, newTab, j, oldCap);
                    else { // preserve order
                        // 链表
                        MyHashMap.Node<K,V> loHead = null, loTail = null;
                        MyHashMap.Node<K,V> hiHead = null, hiTail = null;
                        MyHashMap.Node<K,V> next;
                        do {
                            next = e.next;
                            // 链表切分成高位和低位两部分
                            if ((e.hash & oldCap) == 0) {
                                if (loTail == null)
                                    loHead = e;
                                else
                                    loTail.next = e;
                                loTail = e;
                            }
                            else {
                                if (hiTail == null)
                                    hiHead = e;
                                else
                                    hiTail.next = e;
                                hiTail = e;
                            }
                        } while ((e = next) != null);
                        // 添加到对应的低位
                        if (loTail != null) {
                            loTail.next = null;
                            newTab[j] = loHead;
                        }
                        // 添加到对应的高位
                        if (hiTail != null) {
                            hiTail.next = null;
                            newTab[j + oldCap] = hiHead;
                        }
                    }
                }
            }
        }
        return newTab;
    }

    public Set<Entry<K, V>> entrySet() {
        return null;
    }

    /* ------------ 红黑树相关 ---------------------*/

    /**
     * 升级红黑树，或者扩张Hash表
     * Replaces all linked nodes in bin at index for given hash unless
     * table is too small, in which case resizes instead.
     */
    final void treeifyBin(Node<K,V>[] tab, int hash) {
        int n, index; Node<K,V> e;
        if (tab == null || (n = tab.length) < MIN_TREEIFY_CAPACITY)
            // 扩展数组长度，从而减少链表长度
            resize();
        else if ((e = tab[index = (n - 1) & hash]) != null) {
            TreeNode<K,V> hd = null, tl = null;
            do {
                // 转换为树节点，先建立为双向链表
                TreeNode<K,V> p = replacementTreeNode(e, null);
                if (tl == null)
                    hd = p;
                else {
                    p.prev = tl;
                    tl.next = p;
                }
                tl = p;
            } while ((e = e.next) != null);
            if ((tab[index] = hd) != null)
                // 调整为红黑树
                hd.treeify(tab);
        }
    }

    // For treeifyBin
    MyHashMap.TreeNode<K,V> replacementTreeNode(MyHashMap.Node<K,V> p, MyHashMap.Node<K,V> next) {
        return new MyHashMap.TreeNode<>(p.hash, p.key, p.value, next);
    }


    /**
     * Returns x's Class if it is of the form "class C implements
     * Comparable<C>", else null.
     */
    static Class<?> comparableClassFor(Object x) {
        if (x instanceof Comparable) {
            Class<?> c; Type[] ts, as; Type t; ParameterizedType p;
            if ((c = x.getClass()) == String.class) // bypass checks
                return c;
            if ((ts = c.getGenericInterfaces()) != null) {
                for (int i = 0; i < ts.length; ++i) {
                    if (((t = ts[i]) instanceof ParameterizedType) &&
                            ((p = (ParameterizedType)t).getRawType() ==
                                    Comparable.class) &&
                            (as = p.getActualTypeArguments()) != null &&
                            as.length == 1 && as[0] == c) // type arg is c
                        return c;
                }
            }
        }
        return null;
    }

    /**
     * Returns k.compareTo(x) if x matches kc (k's screened comparable
     * class), else 0.
     */
    @SuppressWarnings({"rawtypes","unchecked"}) // for cast to Comparable
    static int compareComparables(Class<?> kc, Object k, Object x) {
        return (x == null || x.getClass() != kc ? 0 :
                ((Comparable)k).compareTo(x));
    }

    // Create a tree bin node
    MyHashMap.TreeNode<K,V> newTreeNode(int hash, K key, V value, MyHashMap.Node<K,V> next) {
        return new MyHashMap.TreeNode<>(hash, key, value, next);
    }

    // For conversion from TreeNodes to plain nodes
    MyHashMap.Node<K,V> replacementNode(MyHashMap.Node<K,V> p, MyHashMap.Node<K,V> next) {
        return new MyHashMap.Node<>(p.hash, p.key, p.value, next);
    }

    /* 红黑树节点数量小于等于该值时，退化为链表 */
    static final int UNTREEIFY_THRESHOLD = 6;

    /** 红黑树节点 */
    static final class TreeNode<K,V> extends MyHashMap.Node<K,V> {
        MyHashMap.TreeNode<K,V> parent;  // red-black tree links
        MyHashMap.TreeNode<K,V> left;
        MyHashMap.TreeNode<K,V> right;
        MyHashMap.TreeNode<K,V> prev;    // needed to unlink next upon deletion
        boolean red;
        TreeNode(int hash, K key, V val, Node<K,V> next) {
            super(hash, key, val, next);
        }

        /**
         * Returns root of tree containing this node.
         */
        final MyHashMap.TreeNode<K,V> root() {
            for (MyHashMap.TreeNode<K,V> r = this, p;;) {
                if ((p = r.parent) == null)
                    return r;
                r = p;
            }
        }

        /**
         * Ensures that the given root is the first node of its bin.
         */
        static <K,V> void moveRootToFront(Node<K,V>[] tab, TreeNode<K,V> root) {
            int n;
            if (root != null && tab != null && (n = tab.length) > 0) {
                int index = (n - 1) & root.hash;
                MyHashMap.TreeNode<K,V> first = (MyHashMap.TreeNode<K,V>)tab[index];
                if (root != first) {
                    MyHashMap.Node<K,V> rn;
                    tab[index] = root;
                    MyHashMap.TreeNode<K,V> rp = root.prev;
                    if ((rn = root.next) != null)
                        ((MyHashMap.TreeNode<K,V>)rn).prev = rp;
                    if (rp != null)
                        rp.next = rn;
                    if (first != null)
                        first.prev = root;
                    root.next = first;
                    root.prev = null;
                }
                assert checkInvariants(root);
            }
        }

        /**
         * Finds the node starting at root p with the given hash and key.
         * The kc argument caches comparableClassFor(key) upon first use
         * comparing keys.
         */
        final MyHashMap.TreeNode<K,V> find(int h, Object k, Class<?> kc) {
            MyHashMap.TreeNode<K,V> p = this;
            do {
                int ph, dir; K pk;
                MyHashMap.TreeNode<K,V> pl = p.left, pr = p.right, q;
                if ((ph = p.hash) > h)
                    p = pl;
                else if (ph < h)
                    p = pr;
                else if ((pk = p.key) == k || (k != null && k.equals(pk)))
                    return p;
                else if (pl == null)
                    p = pr;
                else if (pr == null)
                    p = pl;
                else if ((kc != null ||
                        (kc = comparableClassFor(k)) != null) &&
                        (dir = compareComparables(kc, k, pk)) != 0)
                    p = (dir < 0) ? pl : pr;
                else if ((q = pr.find(h, k, kc)) != null)
                    return q;
                else
                    p = pl;
            } while (p != null);
            return null;
        }

        /**
         * Calls find for root node.
         */
        final MyHashMap.TreeNode<K,V> getTreeNode(int h, Object k) {
            return ((parent != null) ? root() : this).find(h, k, null);
        }

        /**
         * Tie-breaking utility for ordering insertions when equal
         * hashCodes and non-comparable. We don't require a total
         * order, just a consistent insertion rule to maintain
         * equivalence across rebalancings. Tie-breaking further than
         * necessary simplifies testing a bit.
         */
        static int tieBreakOrder(Object a, Object b) {
            int d;
            if (a == null || b == null ||
                    (d = a.getClass().getName().
                            compareTo(b.getClass().getName())) == 0)
                d = (System.identityHashCode(a) <= System.identityHashCode(b) ?
                        -1 : 1);
            return d;
        }

        /**
         * Forms tree of the nodes linked from this node.
         * @return root of tree
         */
        final void treeify(Node<K,V>[] tab) {
            MyHashMap.TreeNode<K,V> root = null;
            for (MyHashMap.TreeNode<K,V> x = this, next; x != null; x = next) {
                next = (MyHashMap.TreeNode<K,V>)x.next;
                x.left = x.right = null;
                if (root == null) {
                    // 添加第一个节点作为根节点
                    x.parent = null;
                    x.red = false;
                    root = x;
                }
                else {
                    K k = x.key;
                    int h = x.hash;
                    Class<?> kc = null;
                    for (MyHashMap.TreeNode<K,V> p = root;;) {
                        int dir, ph;
                        K pk = p.key;
                        // 下面的判读都是为了确定大小
                        if ((ph = p.hash) > h)
                            dir = -1;
                        else if (ph < h)
                            dir = 1;
                        else if ((kc == null &&
                                (kc = comparableClassFor(k)) == null) ||
                                (dir = compareComparables(kc, k, pk)) == 0)
                            dir = tieBreakOrder(k, pk);

                        MyHashMap.TreeNode<K,V> xp = p;
                        if ((p = (dir <= 0) ? p.left : p.right) == null) {
                            // 需要的节点恰好是空的，放入，否则继续迭代
                            x.parent = xp;
                            // 小的放左节点
                            if (dir <= 0)
                                xp.left = x;
                            // 大的放右节点
                            else
                                xp.right = x;
                            // 平衡红黑树
                            root = balanceInsertion(root, x);
                            break;
                        }
                    }
                }
            }
            moveRootToFront(tab, root);
        }

        /**
         * 将树转换为链表
         * Returns a list of non-TreeNodes replacing those linked from
         * this node.
         */
        final MyHashMap.Node<K,V> untreeify(MyHashMap<K,V> map) {
            MyHashMap.Node<K,V> hd = null, tl = null;
            for (MyHashMap.Node<K,V> q = this; q != null; q = q.next) {
                MyHashMap.Node<K,V> p = map.replacementNode(q, null);
                if (tl == null)
                    hd = p;
                else
                    tl.next = p;
                tl = p;
            }
            return hd;
        }

        /**
         * Tree version of putVal.
         */
        final MyHashMap.TreeNode<K,V> putTreeVal(MyHashMap<K,V> map, Node<K,V>[] tab,
        int h, K k, V v) {
            Class<?> kc = null;
            boolean searched = false;
            MyHashMap.TreeNode<K,V> root = (parent != null) ? root() : this;
            for (MyHashMap.TreeNode<K,V> p = root;;) {
                int dir, ph; K pk;
                if ((ph = p.hash) > h)
                    dir = -1;
                else if (ph < h)
                    dir = 1;
                else if ((pk = p.key) == k || (k != null && k.equals(pk)))
                    return p;
                else if ((kc == null &&
                        (kc = comparableClassFor(k)) == null) ||
                        (dir = compareComparables(kc, k, pk)) == 0) {
                    if (!searched) {
                        MyHashMap.TreeNode<K,V> q, ch;
                        searched = true;
                        if (((ch = p.left) != null &&
                                (q = ch.find(h, k, kc)) != null) ||
                                ((ch = p.right) != null &&
                                        (q = ch.find(h, k, kc)) != null))
                            return q;
                    }
                    dir = tieBreakOrder(k, pk);
                }

                MyHashMap.TreeNode<K,V> xp = p;
                if ((p = (dir <= 0) ? p.left : p.right) == null) {
                    MyHashMap.Node<K,V> xpn = xp.next;
                    MyHashMap.TreeNode<K,V> x = map.newTreeNode(h, k, v, xpn);
                    if (dir <= 0)
                        xp.left = x;
                    else
                        xp.right = x;
                    xp.next = x;
                    x.parent = x.prev = xp;
                    if (xpn != null)
                        ((MyHashMap.TreeNode<K,V>)xpn).prev = x;
                    moveRootToFront(tab, balanceInsertion(root, x));
                    return null;
                }
            }
        }

        /**
         * Removes the given node, that must be present before this call.
         * This is messier than typical red-black deletion code because we
         * cannot swap the contents of an interior node with a leaf
         * successor that is pinned by "next" pointers that are accessible
         * independently during traversal. So instead we swap the tree
         * linkages. If the current tree appears to have too few nodes,
         * the bin is converted back to a plain bin. (The test triggers
         * somewhere between 2 and 6 nodes, depending on tree structure).
         */
        final void removeTreeNode(MyHashMap<K,V> map, Node<K,V>[] tab,
        boolean movable) {
            int n;
            if (tab == null || (n = tab.length) == 0)
                return;
            int index = (n - 1) & hash;
            MyHashMap.TreeNode<K,V> first = (MyHashMap.TreeNode<K,V>)tab[index], root = first, rl;
            MyHashMap.TreeNode<K,V> succ = (MyHashMap.TreeNode<K,V>)next, pred = prev;
            if (pred == null)
                tab[index] = first = succ;
            else
                pred.next = succ;
            if (succ != null)
                succ.prev = pred;
            if (first == null)
                return;
            if (root.parent != null)
                root = root.root();
            if (root == null || root.right == null ||
                    (rl = root.left) == null || rl.left == null) {
                tab[index] = first.untreeify(map);  // too small
                return;
            }
            MyHashMap.TreeNode<K,V> p = this, pl = left, pr = right, replacement;
            if (pl != null && pr != null) {
                MyHashMap.TreeNode<K,V> s = pr, sl;
                while ((sl = s.left) != null) // find successor
                    s = sl;
                boolean c = s.red; s.red = p.red; p.red = c; // swap colors
                MyHashMap.TreeNode<K,V> sr = s.right;
                MyHashMap.TreeNode<K,V> pp = p.parent;
                if (s == pr) { // p was s's direct parent
                    p.parent = s;
                    s.right = p;
                }
                else {
                    MyHashMap.TreeNode<K,V> sp = s.parent;
                    if ((p.parent = sp) != null) {
                        if (s == sp.left)
                            sp.left = p;
                        else
                            sp.right = p;
                    }
                    if ((s.right = pr) != null)
                        pr.parent = s;
                }
                p.left = null;
                if ((p.right = sr) != null)
                    sr.parent = p;
                if ((s.left = pl) != null)
                    pl.parent = s;
                if ((s.parent = pp) == null)
                    root = s;
                else if (p == pp.left)
                    pp.left = s;
                else
                    pp.right = s;
                if (sr != null)
                    replacement = sr;
                else
                    replacement = p;
            }
            else if (pl != null)
                replacement = pl;
            else if (pr != null)
                replacement = pr;
            else
                replacement = p;
            if (replacement != p) {
                MyHashMap.TreeNode<K,V> pp = replacement.parent = p.parent;
                if (pp == null)
                    root = replacement;
                else if (p == pp.left)
                    pp.left = replacement;
                else
                    pp.right = replacement;
                p.left = p.right = p.parent = null;
            }

            MyHashMap.TreeNode<K,V> r = p.red ? root : balanceDeletion(root, replacement);

            if (replacement == p) {  // detach
                MyHashMap.TreeNode<K,V> pp = p.parent;
                p.parent = null;
                if (pp != null) {
                    if (p == pp.left)
                        pp.left = null;
                    else if (p == pp.right)
                        pp.right = null;
                }
            }
            if (movable)
                moveRootToFront(tab, r);
        }

        /**
         * Splits nodes in a tree bin into lower and upper tree bins,
         * or untreeifies if now too small. Called only from resize;
         * see above discussion about split bits and indices.
         *
         * @param map the map
         * @param tab the table for recording bin heads
         * @param index the index of the table being split
         * @param bit the bit of hash to split on
         */
        final void split(MyHashMap<K,V> map, Node<K,V>[] tab, int index, int bit) {
            MyHashMap.TreeNode<K,V> b = this;
            // Relink into lo and hi lists, preserving order
            MyHashMap.TreeNode<K,V> loHead = null, loTail = null;
            MyHashMap.TreeNode<K,V> hiHead = null, hiTail = null;
            int lc = 0, hc = 0;
            for (MyHashMap.TreeNode<K,V> e = b, next; e != null; e = next) {
                next = (MyHashMap.TreeNode<K,V>)e.next;
                e.next = null;
                if ((e.hash & bit) == 0) {
                    if ((e.prev = loTail) == null)
                        loHead = e;
                    else
                        loTail.next = e;
                    loTail = e;
                    ++lc;
                }
                else {
                    if ((e.prev = hiTail) == null)
                        hiHead = e;
                    else
                        hiTail.next = e;
                    hiTail = e;
                    ++hc;
                }
            }

            if (loHead != null) {
                if (lc <= UNTREEIFY_THRESHOLD)
                    tab[index] = loHead.untreeify(map);
                else {
                    tab[index] = loHead;
                    if (hiHead != null) // (else is already treeified)
                        loHead.treeify(tab);
                }
            }
            if (hiHead != null) {
                if (hc <= UNTREEIFY_THRESHOLD)
                    tab[index + bit] = hiHead.untreeify(map);
                else {
                    tab[index + bit] = hiHead;
                    if (loHead != null)
                        hiHead.treeify(tab);
                }
            }
        }

        /* ------------------------------------------------------------ */
        // Red-black tree methods, all adapted from CLR


        /**
         * 左旋转(r，p是红色节点)
         *       pp          pp
         *      /           /
         *     p    -->    r
         *    /\          /
         *   l r         p
         *    /          \
         *   rl          rl
         */
        static <K,V> TreeNode<K,V> rotateLeft(TreeNode<K,V> root,
                                              TreeNode<K,V> p) {
            MyHashMap.TreeNode<K,V> r, pp, rl;
            if (p != null && (r = p.right) != null) {
                if ((rl = p.right = r.left) != null) // 插入是一般是null
                    rl.parent = p;
                if ((pp = r.parent = p.parent) == null)
                    (root = r).red = false;
                else if (pp.left == p)
                    pp.left = r;
                else
                    pp.right = r;
                r.left = p;
                p.parent = r;
            }
            return root;
        }


        static <K,V> TreeNode<K,V> rotateRight(TreeNode<K,V> root,
                                               TreeNode<K,V> p) {
            MyHashMap.TreeNode<K,V> l, pp, lr;
            if (p != null && (l = p.left) != null) {
                if ((lr = p.left = l.right) != null)
                    lr.parent = p;
                if ((pp = l.parent = p.parent) == null)
                    (root = l).red = false;
                else if (pp.right == p)
                    pp.right = l;
                else
                    pp.left = l;
                l.right = p;
                p.parent = l;
            }
            return root;
        }

        /**
         *
         */
        static <K,V> TreeNode<K,V> balanceInsertion(TreeNode<K,V> root,
                                                    TreeNode<K,V> x) {
            // 每一个新插入的节点必然是红色
            x.red = true;
            for (MyHashMap.TreeNode<K,V> xp, xpp, xppl, xppr;;) {
                if ((xp = x.parent) == null) {
                    // 没有父节点，则自己作为根节点，根节点必须是黑色
                    x.red = false;
                    return x;
                }
                else if (!xp.red || (xpp = xp.parent) == null)
                    // 不是连续的两个红色节点，作为红色节点也没有问题，不需要平衡
                    return root;
                if (xp == (xppl = xpp.left)) {
                    // 父亲节点是祖父节点的左孩子
                    if ((xppr = xpp.right) != null && xppr.red) {
                        // 祖父右节点不为空，且右节点是红色
                        // 强制转换颜色，父亲、叔父节点转为黑色，祖父转为红色，并将祖父赋值为当前节点
                        xppr.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    }
                    else {
                        if (x == xp.right) {
                            // 当前节点是父节点的右孩子
                            // 左旋转
                            root = rotateLeft(root, x = xp);
                            // 建立当前节点到原祖父节点的连接
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        if (xp != null) {
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root = rotateRight(root, xpp);
                            }
                        }
                    }
                }
                else {
                    // 父亲节点是祖父节点的右孩子
                    if (xppl != null && xppl.red) {
                        // 祖父左节点不为空，且左节点是红色
                        // 强制转换颜色，父亲、叔父节点转为黑色，祖父转为红色，并将祖父赋值为当前节点
                        xppl.red = false;
                        xp.red = false;
                        xpp.red = true;
                        x = xpp;
                    }
                    else {
                        if (x == xp.left) {
                            root = rotateRight(root, x = xp);
                            xpp = (xp = x.parent) == null ? null : xp.parent;
                        }
                        if (xp != null) {
                            xp.red = false;
                            if (xpp != null) {
                                xpp.red = true;
                                root = rotateLeft(root, xpp);
                            }
                        }
                    }
                }
            }
        }

        static <K,V> TreeNode<K,V> balanceDeletion(TreeNode<K,V> root,
                                                   TreeNode<K,V> x) {
            for (MyHashMap.TreeNode<K,V> xp, xpl, xpr;;)  {
                if (x == null || x == root)
                    return root;
                else if ((xp = x.parent) == null) {
                    x.red = false;
                    return x;
                }
                else if (x.red) {
                    x.red = false;
                    return root;
                }
                else if ((xpl = xp.left) == x) {
                    if ((xpr = xp.right) != null && xpr.red) {
                        xpr.red = false;
                        xp.red = true;
                        root = rotateLeft(root, xp);
                        xpr = (xp = x.parent) == null ? null : xp.right;
                    }
                    if (xpr == null)
                        x = xp;
                    else {
                        MyHashMap.TreeNode<K,V> sl = xpr.left, sr = xpr.right;
                        if ((sr == null || !sr.red) &&
                                (sl == null || !sl.red)) {
                            xpr.red = true;
                            x = xp;
                        }
                        else {
                            if (sr == null || !sr.red) {
                                if (sl != null)
                                    sl.red = false;
                                xpr.red = true;
                                root = rotateRight(root, xpr);
                                xpr = (xp = x.parent) == null ?
                                        null : xp.right;
                            }
                            if (xpr != null) {
                                xpr.red = (xp == null) ? false : xp.red;
                                if ((sr = xpr.right) != null)
                                    sr.red = false;
                            }
                            if (xp != null) {
                                xp.red = false;
                                root = rotateLeft(root, xp);
                            }
                            x = root;
                        }
                    }
                }
                else { // symmetric
                    if (xpl != null && xpl.red) {
                        xpl.red = false;
                        xp.red = true;
                        root = rotateRight(root, xp);
                        xpl = (xp = x.parent) == null ? null : xp.left;
                    }
                    if (xpl == null)
                        x = xp;
                    else {
                        MyHashMap.TreeNode<K,V> sl = xpl.left, sr = xpl.right;
                        if ((sl == null || !sl.red) &&
                                (sr == null || !sr.red)) {
                            xpl.red = true;
                            x = xp;
                        }
                        else {
                            if (sl == null || !sl.red) {
                                if (sr != null)
                                    sr.red = false;
                                xpl.red = true;
                                root = rotateLeft(root, xpl);
                                xpl = (xp = x.parent) == null ?
                                        null : xp.left;
                            }
                            if (xpl != null) {
                                xpl.red = (xp == null) ? false : xp.red;
                                if ((sl = xpl.left) != null)
                                    sl.red = false;
                            }
                            if (xp != null) {
                                xp.red = false;
                                root = rotateRight(root, xp);
                            }
                            x = root;
                        }
                    }
                }
            }
        }

        /**
         * Recursive invariant check
         */
        static <K,V> boolean checkInvariants(TreeNode<K,V> t) {
            MyHashMap.TreeNode<K,V> tp = t.parent, tl = t.left, tr = t.right,
                    tb = t.prev, tn = (MyHashMap.TreeNode<K,V>)t.next;
            if (tb != null && tb.next != t)
                return false;
            if (tn != null && tn.prev != t)
                return false;
            if (tp != null && t != tp.left && t != tp.right)
                return false;
            if (tl != null && (tl.parent != t || tl.hash > t.hash))
                return false;
            if (tr != null && (tr.parent != t || tr.hash < t.hash))
                return false;
            if (t.red && tl != null && tl.red && tr != null && tr.red)
                return false;
            if (tl != null && !checkInvariants(tl))
                return false;
            if (tr != null && !checkInvariants(tr))
                return false;
            return true;
        }
    }

    public static void main(String[] args) {
        System.out.println(tableSizeFor(31));
    }
}
