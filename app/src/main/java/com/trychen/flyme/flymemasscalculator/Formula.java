package com.trychen.flyme.flymemasscalculator;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Formula {

    public ArrayList<String> symbs = new ArrayList<String>();
    public ArrayList<Double> vals = new ArrayList<Double>();
    public ArrayList<Integer> nums = new ArrayList<Integer>();
    public ArrayList<Integer> inds = new ArrayList<Integer>();
    public double mass = 0.0;
    public int nMasses = 0;

    int lastInd = -1;

    public Formula() {
    }

    public Formula(Formula f) {
        symbs = new ArrayList<String>(f.symbs);
        vals = new ArrayList<Double>(f.vals);
        nums = new ArrayList<Integer>(f.nums);
        inds = new ArrayList<Integer>(f.inds);
        mass = f.mass;
        nMasses = f.nMasses;
    }

    public boolean addSym(String sym) {
        lastInd = indexOf(symbols, sym);
        symbs.add(sym);
        inds.add(lastInd);
        return lastInd != -1;
    }

    public Formula addNum(int num) {
        nums.add(num);
        double new_mass = Main.application.isSimpleMode() ? ((double) num) * simpleweights[lastInd] : ((double) num) * weights[lastInd];
        mass += new_mass;
        vals.add(new_mass);
        nMasses += 1;
        return this;
    }

    private int indexOf(String[] arr, String x) {
        for (int i = 0; i < arr.length; i += 1) {
            if (arr[i].compareTo(x) == 0)
                return i;
        }
        return -1;
    }

    public double getMass() {
        return mass;
    }

    public String getSymb(int i) {
        return symbs.get(i);
    }

    public int getNum(int i) {
        return nums.get(i);
    }

    public double getVal(int i) {
        return vals.get(i);
    }

    public String getValTwoDString(int i) {
        return twoDForm.format(vals.get(i));
    }

    public String getName(int i) {
        return names[inds.get(i)];
    }

    public ArrayList<String> getMassPercents() {
        ArrayList<String> s = new ArrayList<String>();
        ArrayList<Double> v = new ArrayList<Double>();
        for (int i = 0; i < nMasses; i += 1) {
            String sym = getName(i);
            int ind = s.indexOf(sym);
            if (ind == -1) {
                s.add(sym);
                v.add(getVal(i));
            } else {
                v.set(ind, v.get(ind) + getVal(i));
            }
        }
        for (int i = 0; i < s.size(); i += 1) {
            s.set(i, formatMassPercent(s.get(i), v.get(i)));
        }
        return s;
    }

    private DecimalFormat twoDForm = new DecimalFormat("#.##");

    public String formatMassPercent(String sym, double val) {
        String ret = sym;
        ret += "  ";
        double per = val / mass;
        per *= 100.0;
        per = Double.valueOf(twoDForm.format(per));
        ret += Double.toString(per) + "%";
        return ret;
    }

    public double getMassPercent(double val) {
        double per = val / mass;
        per *= 100.0;
        per = Double.valueOf(twoDForm.format(per));
        return per;
    }

    public String getMassPercentInFraction(double val) {
        double mass = this.mass;
        double p = val;

        int m,v;
        if ((mass - (int)mass)==0){
            m= (int) mass;
        } else {
            mass = mass*2;
            p = val*2;

            m= (int) mass;
            v= (int) p;
        }

        if ((p - (int)p)==0){
            v= (int) p;
        } else {
            mass = mass*2;
            p = val*2;

            v = (int) p;
        }

        return fraction(v,m);
    }

    public static String fraction(int fenzi, int fenmu) {
        int fz = Math.abs(fenzi);
        int fm = Math.abs(fenmu);
        int mod = fz % fm;
        while (mod > 0) { //求分子分母的最大公因数
            fz = fm;
            fm = mod;
            mod = fz % fm;
        }
        return fenzi / fm + "/" + fenmu / fm; //分子分母都除以最大公因数
    }

    public String getCleanForm() {
        String ret = "";
        int n;
        for (int i = 0; i < nMasses; i += 1) {
            ret += getName(i);
            n = nums.get(i);
            if (n > 1)
                ret += Integer.toString(n);
        }
        return ret;
    }

    public String toString() {
        return getCleanForm();
    }

    private static final String[] symbols = {"h", "he", "li", "be", "b", "c", "n", "o", "f", "ne", "na", "mg", "al", "si", "p", "s", "cl", "ar", "k", "ca", "sc", "ti", "v", "cr", "mn", "fe", "co", "ni", "cu", "zn", "ga", "ge", "as", "se", "br", "kr", "rb", "sr", "y", "zr", "nb", "mo", "tc", "ru", "rh", "pd", "ag", "cd", "in", "sn", "sb", "te", "i", "xe", "cs", "ba", "la", "ce", "pr", "nd", "pm", "sm", "eu", "gd", "tb", "dy", "ho", "er", "tm", "yb", "lu", "hf", "ta", "w", "re", "os", "ir", "pt", "au", "hg", "tl", "pb", "bi", "po", "at", "rn", "fr", "ra", "ac", "th", "pa", "u", "np", "pu", "am", "cm", "bk", "cf", "es", "fm", "md", "no", "lr", "rf", "db", "sg", "bh", "hs", "mt", "ds", "rg"};
    private static final String[] names = {"H", "He", "Li", "Be", "B", "C", "N", "O", "F", "Ne", "Na", "Mg", "Al", "Si", "P", "S", "Cl", "Ar", "K", "Ca", "Sc", "Ti", "V", "Cr", "Mn", "Fe", "Co", "Ni", "Cu", "Zn", "Ga", "Ge", "As", "Se", "Br", "Kr", "Rb", "Sr", "Y", "Zr", "Nb", "Mo", "Tc", "Ru", "Rh", "Pd", "Ag", "Cd", "In", "Sn", "Sb", "Te", "I", "Xe", "Cs", "Ba", "La", "Ce", "Pr", "Nd", "Pm", "Sm", "Eu", "Gd", "Tb", "Dy", "Ho", "Er", "Tm", "Yb", "Lu", "Hf", "Ta", "W", "Re", "Os", "Ir", "Pt", "Au", "Hg", "Tl", "Pb", "Bi", "Po", "At", "Rn", "Fr", "Ra", "Ac", "Th", "Pa", "U", "Np", "Pu", "Am", "Cm", "Bk", "Cf", "Es", "Fm", "Md", "No", "Lr", "Rf", "Db", "Sg", "Bh", "Hs", "Mt", "Ds", "Rg"};
    private static final double[] weights = {1.00794, 4.002602, 6.941, 9.012182, 10.811, 12.0107, 14.0067, 15.9994, 18.9984032, 20.1797, 22.98976928, 24.305, 26.9815386, 28.0855, 30.973762, 32.065, 35.453, 39.948, 39.0983, 40.078, 44.955912, 47.867, 50.9415, 51.9961, 54.938045, 55.845, 58.933195, 58.6934, 63.546, 65.38, 69.723, 72.64, 74.9216, 78.96, 79.904, 83.798, 85.4678, 87.62, 88.90585, 91.224, 92.90638, 95.96, 98.0, 101.07, 102.9055, 106.42, 107.8682, 112.411, 114.818, 118.71, 121.76, 127.6, 126.90447, 131.293, 132.9054519, 137.327, 138.90547, 140.116, 140.90765, 144.242, 145.0, 150.36, 151.964, 157.25, 158.92535, 162.5, 164.93032, 167.259, 168.93421, 173.054, 174.9668, 178.49, 180.94788, 183.84, 186.207, 190.23, 192.217, 195.084, 196.966569, 200.59, 204.3833, 207.2, 208.9804, 209.0, 210.0, 222.0, 223.0, 226.0, 227.0, 232.03806, 231.03588, 238.02891, 237.0, 244.0, 243.0, 247.0, 247.0, 251.0, 252.0, 257.0, 258.0, 259.0, 262.0, 267.0, 268.0, 271.0, 272.0, 270.0, 276.0, 281.0, 280.0};
    private static final double[] simpleweights = {1, 4, 7, 9, 11, 12, 14, 16, 19, 20, 23, 24, 27, 28, 31, 32, 35.5, 40, 39, 40, 45, 48, 51, 52, 55, 56, 59, 59, 64, 65, 70, 73, 75, 79, 80, 84, 85, 88, 89, 92, 93, 96, 98, 101, 103, 106, 108, 112, 115, 119, 122, 128, 127, 131, 133, 137, 139, 140, 141, 144, 145, 150, 152, 157, 159, 162.5, 165, 167, 169, 173, 175, 178.49, 181, 184, 186, 190, 192, 195, 197, 201, 204, 207, 209, 209, 210, 222, 223, 226, 227, 232, 231, 238, 237, 244, 243, 247, 247, 251, 252, 257, 258, 259, 262, 267, 268, 271, 272, 270, 276, 281, 280};
    private static final String[] chinese = {""};
}
