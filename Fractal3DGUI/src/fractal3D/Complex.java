package fractal3D;

/**
Complex implements a complex number and defines complex
arithmetic and mathematical functions
Last Updated February 27, 2001
Copyright 1997-2001
@version 1.0
@author Andrew G. Bennett
*/

public class Complex {

    private double x,y;
    
    public Complex(double u,double v) {
        x=u;
        y=v;
    }
    
    public double real() {
        return x;
    }
    
    public double imag() {
        return y;
    }
    
    public double mod() {
        if (x!=0 || y!=0) {
            return Math.sqrt(x*x+y*y);
        } else {
            return 0d;
        }
    }
    
	public double mod2() {
	    if (x!=0 || y!=0) {
	        return x*x+y*y;
	    } else {
	        return 0d;
	    }
	}
    
    public double arg() {
        return Math.atan2(y,x);
    }
    
    public Complex conj() {
        return new Complex(x,-y);
    }
    
    public Complex plus(Complex w) {
        return new Complex(x+w.real(),y+w.imag());
    }
    
    public Complex minus(Complex w) {
        return new Complex(x-w.real(),y-w.imag());
    }
    
    public Complex times(Complex w) {
        return new Complex(x*w.real()-y*w.imag(),x*w.imag()+y*w.real());
    }
    
	public Complex pow(int p) {
		int i = p;
		Complex z1 = new Complex(x,y);
		Complex zn = new Complex(x,y);
		while (i > 1) {
			zn = zn.times(z1);
			i--;
		}
	    return zn;
	}

    public Complex div(Complex w) {
        double den=Math.pow(w.mod(),2);
        return new Complex((x*w.real()+y*w.imag())/den,(y*w.real()-x*w.imag())/den);
    }
    
    public Complex exp() {
        return new Complex(Math.exp(x)*Math.cos(y),Math.exp(x)*Math.sin(y));
    }
    
    public Complex log() {
        return new Complex(Math.log(this.mod()),this.arg());
    }
    
    public Complex sqrt() {
        double r=Math.sqrt(this.mod());
        double theta=this.arg()/2;
        return new Complex(r*Math.cos(theta),r*Math.sin(theta));
    }
    
    private double cosh(double theta) {
        return (Math.exp(theta)+Math.exp(-theta))/2;
    }
    
    private double sinh(double theta) {
        return (Math.exp(theta)-Math.exp(-theta))/2;
    }
    
    public Complex sin() {
        return new Complex(cosh(y)*Math.sin(x),sinh(y)*Math.cos(x));
    }
    
    public Complex cos() {
        return new Complex(cosh(y)*Math.cos(x),-sinh(y)*Math.sin(x));
    }
    
    public Complex sinh() {
        return new Complex(sinh(x)*Math.cos(y),cosh(x)*Math.sin(y));
    }
    
    public Complex cosh() {
        return new Complex(cosh(x)*Math.cos(y),sinh(x)*Math.sin(y));
    }
    
    public Complex tan() {
        return (this.sin()).div(this.cos());
    }
    
    public Complex chs() {
        return new Complex(-x,-y);
    }
    
    public String toString() {
        if (x!=0 && y>0) {
            return x+" + "+y+"i";
        }
        if (x!=0 && y<0) {
            return x+" - "+(-y)+"i";
        }
        if (y==0) {
            return String.valueOf(x);
        }
        if (x==0) {
            return y+"i";
        }

        return x+" + i*"+y;
        
    }       
}
