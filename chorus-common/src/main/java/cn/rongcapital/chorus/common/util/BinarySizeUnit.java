package cn.rongcapital.chorus.common.util;

/**
 * @author yimin
 */
public enum BinarySizeUnit {
    BYTES(0) {
        public long bytes(long d) {return d;}

        public long kilo(long d) {return d / C1 / C0;}

        public long mega(long d) { return d / (C2 / C0);}

        public long giga(long d) { return d / (C3 / C0);}

        public long tera(long d) { return d / (C4 / C0);}

        public long peta(long d) { return d / (C5 / C0);}

        public long exa(long d) { return d / (C6 / C0);}

        public long convert(long d, BinarySizeUnit u) { return u.bytes(d);}
    },
    KILO(10) {
        public long bytes(long d) {return x(d, C1 / C0, MAX / C1 / C0);}

        public long kilo(long d) {return d;}

        public long mega(long d) { return d / (C2 / C1);}

        public long giga(long d) { return d / (C3 / C1);}

        public long tera(long d) { return d / (C4 / C1);}

        public long peta(long d) { return d / (C5 / C1);}

        public long exa(long d) { return d / (C6 / C1);}

        public long convert(long d, BinarySizeUnit u) { return u.kilo(d);}
    },
    MEGA(KILO.bitShift + 10) {
        public long bytes(long d) {return x(d, C2 / C0, MAX / C2 / C0);}

        public long kilo(long d) {return x(d, C2 / C1, MAX / (C2 / C1));}

        public long mega(long d) { return d;}

        public long giga(long d) { return d / (C3 / C2);}

        public long tera(long d) { return d / (C4 / C2);}

        public long peta(long d) { return d / (C5 / C2);}

        public long exa(long d) { return d / (C6 / C2);}

        public long convert(long d, BinarySizeUnit u) { return u.mega(d);}
    },
    GIGA(MEGA.bitShift + 10) {
        public long bytes(long d) {return x(d, C3 / C0, MAX / C3 / C0);}

        public long kilo(long d) {return x(d, C3 / C1, MAX / (C3 / C1)); }

        public long mega(long d) {return x(d, C3 / C2, MAX / (C3 / C2)); }

        public long giga(long d) {return d; }

        public long tera(long d) {return d / (C4 / C3); }

        public long peta(long d) {return d / (C5 / C3); }

        public long exa(long d) {return d / (C6 / C3); }

        public long convert(long d, BinarySizeUnit u) { return u.giga(d);}
    },
    TERA(GIGA.bitShift + 10) {
        public long bytes(long d) {return x(d, C4 / C0, MAX / C4 / C0);}

        public long kilo(long d) {return x(d, C4 / C1, MAX / (C4 / C1)); }

        public long mega(long d) {return x(d, C4 / C2, MAX / (C4 / C2)); }

        public long giga(long d) {return x(d, C4 / C3, MAX / (C4 / C3)); }

        public long tera(long d) {return d; }

        public long peta(long d) {return d / (C5 / C4); }

        public long exa(long d) {return d / (C6 / C4); }

        public long convert(long d, BinarySizeUnit u) { return u.tera(d);}
    },
    PETA(TERA.bitShift + 10) {
        public long bytes(long d) {return x(d, C5 / C0, MAX / C5 / C0);}

        public long kilo(long d) { return x(d, C5 / C1, MAX / (C5 / C1));}

        public long mega(long d) { return x(d, C5 / C2, MAX / (C5 / C2));}

        public long giga(long d) { return x(d, C5 / C3, MAX / (C5 / C3));}

        public long tera(long d) { return x(d, C5 / C4, MAX / (C5 / C4));}

        public long peta(long d) { return d;}

        public long exa(long d) { return d / (C6 / C5);}

        public long convert(long d, BinarySizeUnit u) { return u.peta(d);}
    },

    EXA(PETA.bitShift + 10) {
        public long bytes(long d) {return x(d, C6 / C0, MAX / C6 / C0);}

        public long kilo(long d) {return x(d, C6 / C1, MAX / (C6 / C1)); }

        public long mega(long d) {return x(d, C6 / C2, MAX / (C6 / C2)); }

        public long giga(long d) {return x(d, C6 / C3, MAX / (C6 / C3)); }

        public long tera(long d) {return x(d, C6 / C4, MAX / (C6 / C4)); }

        public long peta(long d) {return x(d, C6 / C5, MAX / (C6 / C5)); }

        public long exa(long d) {return d; }

        public long convert(long d, BinarySizeUnit u) { return u.exa(d);}
    };
    public final long value;
    public final char symbol;
    public final int  bitShift;
    public final long bitMask;

    BinarySizeUnit(int bitShift) {
        this.bitShift = bitShift;
        this.value = 1L << bitShift;
        this.bitMask = this.value - 1L;
        this.symbol = toString().charAt(0);
    }

    static final long C0 = 1L;
    static final long C1 = C0 * 1024L;
    static final long C2 = C1 * 1024L;
    static final long C3 = C2 * 1024L;
    static final long C4 = C3 * 1024L;
    static final long C5 = C4 * 1024L;
    static final long C6 = C5 * 1024L;

    static final long MAX = Long.MAX_VALUE;

    static long x(long d, long m, long over) {
        if (d > over) return Long.MAX_VALUE;
        if (d < -over) return Long.MIN_VALUE;
        return d * m;
    }

    public long bytes(long d) {
        throw new AbstractMethodError();
    }

    public long kilo(long d) {
        throw new AbstractMethodError();
    }

    public long mega(long d) {
        throw new AbstractMethodError();
    }

    public long giga(long d) {
        throw new AbstractMethodError();
    }

    public long tera(long d) {
        throw new AbstractMethodError();
    }

    public long peta(long d) {
        throw new AbstractMethodError();
    }

    public long exa(long d) {
        throw new AbstractMethodError();
    }

    public long convert(long d, BinarySizeUnit u) {
        throw new AbstractMethodError();
    }
}
