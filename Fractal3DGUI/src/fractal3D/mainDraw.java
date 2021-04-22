package fractal3D;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Random;

import javax.swing.JComponent;

//MainDraw class or for drawing fractal
@SuppressWarnings("serial")
class mainDraw extends JComponent {

	private static Graphics2D g2d;
	private static int window_width;
	private static int window_height;
	
	// View parameters
	private static double[] rotation0 = {0,-70};
	private static double[] rotation = {0,-70};
	private static double[] position0 = {0,-30};
	private static double[] position = {0,-30};
	private static double camera_distance = 1000;
	private static double object_scale = 1;
	private static double box_size = 500;

	// Fractal settings
	private static int fractal_iterations = 1000;
	private static int fractal_resolution = 150;
	private static int fractal_samples = 16;
	private static int fractal_power = 2; // changes power in fractal equation z = z^2 + c
	
	// Colors
	public static Color fractal_color = new Color(100, 100, 255, 50);
	public static Color center_color = new Color(255, 0, 0, 100);
	public static Color box_color = new Color(255, 255, 255, 50);

	// GUI control parameters
	public static int pixel_size = 3;
	public static Boolean perspective_projection = true;
	
	// Fractal data
	private static double[][] fractal_data_3D = genFractal3D();
	private static double[][] fractal_data_2D = genFractal2D();
	

	// Paint method for drawing fractal
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		// Get frame size
	    Rectangle window = Fractal3DGUI.frame.getBounds();
	    window_width = window.width;
	    window_height = window.height;	    
		
	    // Setup and draw shapes
		g2d = (Graphics2D) g;
		
	    g2d.fillRect(0, 0, window_width, window_height);

		draw2DFractal(g2d);
		draw3DFractal(g2d);
		drawCube(g2d);

	}
	
	// Method to move fractal
	public void moveXY(double dx, double dy) {
		position[0] = (int)(position0[0]+dx*2/object_scale);
		position[1] = (int)(position0[1]+dy*2/object_scale);
		repaint();
	}
	
	// Method to set initial position after moving
	public void setXY() {
		position0[0] = position[0];
		position0[1] = position[1];
	}
	
	// Method to rotate fractal
	public void moveTheta(double tx, double ty) {
		rotation[0] = tx + rotation0[0];
		rotation[1] = ty + rotation0[1];
		rotation[1] = (rotation[1] > 0) ? 0 : rotation[1];
		rotation[1] = (rotation[1] < -180) ? -180 : rotation[1];
		repaint();
	}
	
	// Method to set initial rotation after rotating
	public void setTheta() {
		rotation0[0] = rotation[0];
		rotation0[1] = rotation[1];
	}
	
	// Method to scale fractal
	public void zoom(double factor) {
		double object_scale0 = object_scale;
		object_scale += factor;
		if (object_scale < 0.1 || object_scale > 10) {
			object_scale = object_scale0;
		}
		repaint();
	}
	
	// Method to move camera closer to fractal (alters perspective)
	public void moveZ(double factor) {
		double initial = camera_distance;
		camera_distance += factor;
		if (camera_distance < box_size || camera_distance > 10000) {
			camera_distance = initial;
		}
		repaint();
	}
	
	// Method to change power of fractal equation z = z^2 + c
	public void changeOrder(int dp) {
		fractal_power = fractal_power + dp;
		fractal_power = (fractal_power < 2) ? 2 : fractal_power;
		fractal_data_3D = genFractal3D();
		fractal_data_2D = genFractal2D();
		repaint();
	}
	
	// Method to enable or disable perspective projection
	public void changePerspective() {
		perspective_projection = !(perspective_projection);
		repaint();
	}
	
	// Method to increase or decrease fractal resolution
	public void changeResolution(int dr) {
		if (fractal_resolution + dr <= 50 || fractal_resolution + dr >= 500)
			return;
		fractal_resolution = fractal_resolution + dr;
		fractal_data_3D = genFractal3D();
		fractal_data_2D = genFractal2D();
		repaint();
	}
	
	// Method to change transparency of fractal body
	public void setTransparency(int tr) {

		int a = tr;
		int r = fractal_color.getRed();
		int g = fractal_color.getGreen();
		int b = fractal_color.getBlue();
		
		a = (a < 0) ? 0 : a;
		a = (a > 255) ? 255 : a;

		fractal_color = new Color(r, g, b, a);
		repaint();
	}
	
	// Method to change transparency of fractal center 
	public void setCenterTransparency(int tr) {

		int a = tr;
		int r = center_color.getRed();
		int g = center_color.getGreen();
		int b = center_color.getBlue();
		
		a = (a < 0) ? 0 : a;
		a = (a > 255) ? 255 : a;

		center_color = new Color(r, g, b, a);
		repaint();
	}
	
	// Method to change pixel size used to draw fractal
	public void setPixelSize(int ps) {
		pixel_size = ps;
		pixel_size = (pixel_size < 1) ? 1 : pixel_size;
		pixel_size = (pixel_size > 25) ? 25 : pixel_size;
		repaint();
	}
	
	
	// === Fractal drawing methods ================================================================
	
	// Method to draw 3D Mandelbrot points
	public void draw3DFractal(Graphics g) {
	    double[] vec1;
		int x1, y1;
		
		g.setColor(fractal_color);
		
		for (int i = 0; i < fractal_data_3D.length; i++) {
			if (fractal_data_3D[i][0] != 0 || fractal_data_3D[i][1] != 0 || fractal_data_3D[i][2] != 0) {
				
				vec1 = matrixRotateZX(fractal_data_3D[i],rotation[0],rotation[1]);
				
				vec1 = translateVector(vec1,position[0],position[1],0);
				
				if (perspective_projection) {
					vec1 = perspective(vec1);
				}

				x1 = (int) Math.ceil(vec1[0]*object_scale+window_width/2-pixel_size/2);
				y1 = (int) Math.ceil(vec1[1]*object_scale+window_height/2-pixel_size/2);
				
				g.fillRect(x1, y1, pixel_size, pixel_size);
				
				if (fractal_data_3D[i][1] == 0) {
					g.setColor(center_color);
					g.fillRect(x1, y1, pixel_size, pixel_size);
					g.setColor(fractal_color);
				}
			}
		}
	}
	
	// Method to draw 2D Mandelbrot image
	public void draw2DFractal(Graphics g) {
		
		// draw image
		double[][] p = new double[4][3];
		double[][] pr = new double[4][3];
		int[] px = new int[4];
		int[] py = new int[4];
		int cr, cg, cb;
		double dw = 0.5*box_size/fractal_resolution;
		
		for (int i = 1; i < fractal_resolution; i++) {
			for (int j = 1; j < fractal_resolution; j++) {
				
				p[0][0] = fractal_data_2D[i*fractal_resolution+j][0]-dw;
				p[0][1] = fractal_data_2D[i*fractal_resolution+j][1]-dw;
				p[0][2] = box_size/2;
				
				p[1][0] = fractal_data_2D[i*fractal_resolution+j][0]-dw;
				p[1][1] = fractal_data_2D[i*fractal_resolution+j][1]+dw;
				p[1][2] = box_size/2;
				
				p[2][0] = fractal_data_2D[i*fractal_resolution+j][0]+dw;
				p[2][1] = fractal_data_2D[i*fractal_resolution+j][1]+dw;
				p[2][2] = box_size/2;
				
				p[3][0] = fractal_data_2D[i*fractal_resolution+j][0]+dw;
				p[3][1] = fractal_data_2D[i*fractal_resolution+j][1]-dw;
				p[3][2] = box_size/2;
	
				pr[0] = matrixRotateZX(p[0],rotation[0],rotation[1]);
				pr[1] = matrixRotateZX(p[1],rotation[0],rotation[1]);
				pr[2] = matrixRotateZX(p[2],rotation[0],rotation[1]);
				pr[3] = matrixRotateZX(p[3],rotation[0],rotation[1]);
				
				pr[0] = translateVector(pr[0],position[0],position[1],0);
				pr[1] = translateVector(pr[1],position[0],position[1],0);
				pr[2] = translateVector(pr[2],position[0],position[1],0);
				pr[3] = translateVector(pr[3],position[0],position[1],0);

				if (perspective_projection) {
					pr[0] = perspective(pr[0]);
					pr[1] = perspective(pr[1]);
					pr[2] = perspective(pr[2]);
					pr[3] = perspective(pr[3]);
				}
				
				px[0] = (int) (pr[0][0]*object_scale)+window_width/2;
				py[0] = (int) (pr[0][1]*object_scale)+window_height/2;
				px[1] = (int) (pr[1][0]*object_scale)+window_width/2;
				py[1] = (int) (pr[1][1]*object_scale)+window_height/2;;
				px[2] = (int) (pr[2][0]*object_scale)+window_width/2;
				py[2] = (int) (pr[2][1]*object_scale)+window_height/2;;
				px[3] = (int) (pr[3][0]*object_scale)+window_width/2;
				py[3] = (int) (pr[3][1]*object_scale)+window_height/2;;
				
				double color = Math.abs(255-fractal_data_2D[i*fractal_resolution+j][2])%255;
				cb = (int) (color);
				cr = (int) (255-color);
				cg = cr;
				
				if (cb > 0) {
					g.setColor(new Color(cr, cg, cb));
					g.fillPolygon(px, py, 4);
				}
			}
		}
	}
	
	// Method to draw wireframe cube
	public static void drawCube(Graphics g) {
		
	    int[][] faces = {
	    		{0,1,2,3},
	    		{1,5,6,2},
	    		{5,4,7,6},
	    		{4,0,3,7},
	    		{3,2,6,7},
	    		{0,4,5,1}};
	    
	    double[][] points = {
	    		{-1,-1,-1},
	    		{ 1,-1,-1},
	    		{ 1, 1,-1},
	    		{-1, 1,-1},
	    		{-1,-1, 1},
	    		{ 1,-1, 1},
	    		{ 1, 1, 1},
	    		{-1, 1, 1}};
	    
		int npoints = faces[0].length;
		int nfaces = faces.length;
		
	    double[][] face = new double[npoints][3];
	    int xarr[] = new int[npoints+1];
	    int yarr[] = new int[npoints+1];
		double s = 0.5*box_size;

		g.setColor(box_color);
		    
 	for (int f=0; f<nfaces; ++f) {
			for(int p=0; p<npoints; ++p) {
				face[p][0] = s*points[(faces[f][p])][0];
				face[p][1] = s*points[(faces[f][p])][1];
				face[p][2] = s*points[(faces[f][p])][2];
				
				face[p] = matrixRotateZX(face[p],rotation[0],rotation[1]);
				
				face[p] = translateVector(face[p],position[0],position[1],0);
				
				if (perspective_projection) {
					face[p] = perspective(face[p]);
				}	
			}
				
			for(int i=0; i<npoints; ++i) {
				xarr[i] = (int) (face[i][0]*object_scale+window_width/2);
				yarr[i] = (int) (face[i][1]*object_scale+window_height/2);
			}
			xarr[npoints] = xarr[0];
			yarr[npoints] = yarr[0];
			
			g.drawPolyline(xarr, yarr, npoints+1); // wireframe cube
 	}
	}
	
	// Method to generate 3D fractal data
	public static double[][] genFractal3D() {
		double z[][] = new double[fractal_resolution*fractal_resolution*fractal_samples][3];
		double xcenter = (fractal_power == 2) ? -0.5 : 0.0;
		double ycenter = 0.0;
		double zcenter = 0;
		double xwidth = 3;
		double ywidth = 3;
		double zwidth = 5;
		double xmin = xcenter - xwidth/2;
		double xmax = xcenter + xwidth/2;
		double ymin = ycenter - ywidth/2;
		double ymax = ycenter + ywidth/2;
		double zmin = zcenter - zwidth/2;
		double zmax = zcenter + zwidth/2;
		double zobject_scale = 1;
		double dx = (xmax-xmin)/fractal_resolution;
		double dy = (ymax-ymin)/fractal_resolution;
		double zx,zy,cx,cy,zf;
		Complex zz, cc;
		int iter = 0;
		int p = 0;
		
		Random rand = new Random();
			
		for (int ix=0; ix<fractal_resolution; ix++) {
			for (int iy=0; iy<fractal_resolution; iy++) {
				zx = zy = 0;
				cx = xmin + ix*dx;
				cy = ymin + iy*dy;
				
				if (Math.abs(cy)>1e-6) {
					cx += (rand.nextDouble()-0.5)*dx;
					cy += (rand.nextDouble()-0.5)*dy;
				}
				
				zz = new Complex(zx,zy);
				cc = new Complex(cx,cy);
				iter = fractal_iterations;
				
				while (zz.mod2() < 4 && iter > 0) {
					zz = zz.pow(fractal_power).plus(cc);
					zf = zobject_scale*zz.real()-zcenter;
	
					if (iter < fractal_samples && zf > zmin && zf < zmax) {
							z[p][0] = (cx-xcenter)*box_size/xwidth;
							z[p][1] = (cy-ycenter)*box_size/ywidth;
							z[p][2] = zf*box_size/zwidth;
							p+=1;
					}
						
					iter--;
				}
			}
		}

		return z;
	}
	
	// Method to generate 2D fractal data
	public static double[][] genFractal2D() {
		double z[][] = new double[fractal_resolution*fractal_resolution][3];
		double xcenter = (fractal_power == 2) ? -0.5 : 0.0;
		double ycenter = 0.0;
		double xwidth = 3;
		double ywidth = 3;
		double xmin = xcenter - xwidth/2;
		double xmax = xcenter + xwidth/2;
		double ymin = ycenter - ywidth/2;
		double ymax = ycenter + ywidth/2;
		double dx = (xmax-xmin)/fractal_resolution;
		double dy = (ymax-ymin)/fractal_resolution;
		double zx,zy,cx,cy;
		Complex zz, cc;
		int iter = 0;
		
		for (int ix=0; ix<fractal_resolution; ix++) {
			for (int iy=0; iy<fractal_resolution; iy++) {
				zx = zy = 0;
				cx = xmin + ix*dx;
				cy = ymin + iy*dy;
				zz = new Complex(zx,zy);
				cc = new Complex(cx,cy);
				iter = fractal_iterations;
				while (zz.mod2() < 4 && iter > 0) {
					zz = zz.pow(fractal_power).plus(cc);
					iter--;
				}
				z[ix*fractal_resolution + iy][0] = (xmin+ix*dx-xcenter)*box_size/xwidth;
				z[ix*fractal_resolution + iy][1] = (ymin+iy*dy-ycenter)*box_size/ywidth;
				z[ix*fractal_resolution + iy][2] = iter | (iter << 9);
			}
		}

		return z;
	}
	
	
	// === Vector operations ======================================================================
	
	// Method to normalize a vector
	private static double[] normalizeVector(double[] vec) {
		double mag = magnitude(vec);
		if (mag != 0) {
			return scaleVector(vec, 1.0/mag);
		} else {
			return vec;
		}
	}
	
	// Method to compute the magnitude of a vector
	private static double magnitude(double[] vec){
		double mag = Math.sqrt(vec[0]*vec[0] + vec[1]*vec[1] + vec[2]*vec[2]);;
		return mag;
	}
		
	
	// Method to translate a vector
	private static double[] translateVector(double[] vec, double x, double y, double z) {
		double vec2[] = {vec[0]+x, vec[1]+y, vec[2]+z};
		return vec2;
	}
	
	// Method to scale a vector
	private static double[] scaleVector(double[] vec, double s) {
		double vec2[] = {vec[0]*s, vec[1]*s, vec[2]*s};
		return vec2;
	}
	
	// Method to subtract vectors
	private static double[] subtractVector(double[] vec1, double[] vec2) {
		double sub[] = {vec1[0]-vec2[0], vec1[1]-vec2[1], vec1[2]-vec2[2]};
		return sub;
	}
	
	// Method to compute the vector dot product
	private static double dotProduct(double[] vec1, double[] vec2){
		double dot = vec1[0]*vec2[0] + vec1[1]*vec2[1] + vec1[2]*vec2[2];
		return dot;
	}
	
	// Method to compute the vector cross product
	private static double[] crossProduct(double[] vec1, double[] vec2){
		double newVec[] = {vec1[1]*vec2[2] - vec1[2]*vec2[1],
				           vec1[2]*vec2[0] - vec1[0]*vec2[2],
				           vec1[0]*vec2[1] - vec1[1]*vec2[0]};
		return newVec;
	}
	
	// Method to compute a simple perspective projection
	private static double[] perspective(double[] vec){
		double bias = 0.1; // prevents polygons from exploding

		double denominator = camera_distance+vec[2]+bias;
				
		// check for division by zero
		double factor = denominator != 0 ? camera_distance/denominator : denominator;
		
		// return perspective transformed vector
		return scaleVector(vec, factor);
	}
	
	
	// === Matrix operations ======================================================================
	
	// rotate about x
	public static double[] matrixRotateX(double[] vec, double rx) {
		double t = Math.toRadians(rx);
		double[][] rMat = {{1, 0, 0}, {0, Math.cos(t), -Math.sin(t)}, {0, Math.sin(t), -Math.cos(t)}};
		double[] newVec = matrixProduct(rMat, vec);
		return newVec;
	}
	
	// rotate about y
	public static double[] matrixRotateY(double[] vec, double ry) {
		double t = Math.toRadians(ry);
		double[][] rMat = {{Math.cos(t), 0, Math.sin(t)}, {0, 1, 0}, {-Math.sin(t), 0, Math.cos(t)}};
		double[] newVec = matrixProduct(rMat, vec);
		return newVec;
	}
	
	// rotate about z
	public static double[] matrixRotateZ(double[] vec, double rz) {
		double t = Math.toRadians(rz);
		double[][] rMat = {{Math.cos(t), -Math.sin(t),0}, {Math.sin(t), Math.cos(t),0}, {0,0,1}};
		double[] newVec = matrixProduct(rMat, vec);
		return newVec;
	}

	// combined rotations about z then x (more efficient)
	public static double[] matrixRotateZX(double[] vec, double rz, double rx) {
		double cz = Math.cos(Math.toRadians(rz));
		double cx = Math.cos(Math.toRadians(rx));
		double sz = Math.sin(Math.toRadians(rz));
		double sx = Math.sin(Math.toRadians(rx));
		double[] newVec = {vec[0]*cz - vec[1]*sz, 
				 vec[1]*cx*cz - vec[2]*sx + vec[0]*cx*sz,
				 vec[2]*cx + vec[1]*cz*sx + vec[0]*sx*sz};
		return newVec;
	}
	
	// generic rotation about specified axis
	public static double[] matrixRotate(double[] vec, double angle, double[] axis) {
		double t = Math.toRadians(angle);
		double object_scale = Math.sqrt(axis[0]*axis[0] + axis[1]*axis[1] + axis[2]*axis[2]);
		double ux = axis[0]/object_scale;
		double uy = axis[1]/object_scale;
		double uz = axis[2]/object_scale;
		double st = Math.sin(t);
		double ct = Math.cos(t);
		double[][] rMat = {{ct+ux*ux*(1-ct), ux*uy*(1-ct)-uz*st, ux*uz*(1-ct)+uy*st},
				 {uy*ux*(1-ct)+uz*st, ct+uy*uy*(1-ct), uy*uz*(1-ct)-ux*st},
				 {uz*ux*(1-ct)-uy*st, ux*uy*(1-ct)+ux*st, ct+uz*uz*(1-ct)}};
				 
		double[] newVec = matrixProduct(rMat, vec);
		return newVec;
	}
	
	// matrix-matrix multiplication
	public static double[][] matrixProduct(double[][] mat1, double[][] mat2) {
		int rows1 = mat1.length;
		int cols1 = mat1[0].length;
		//int rows2 = mat2.length;
		int cols2 = mat2[0].length;
		
		double[][] matProduct = new double[rows1][cols2];
		
		for (int r = 0; r < rows1; ++r) {
			for (int c = 0; c < cols2; ++c) {
				for (int i = 0; i < cols1; ++i) {
					matProduct[r][c] = matProduct[r][c] + mat1[r][i]*mat2[i][c];
				}
			}
		}
		
		return matProduct;
	}
	
	// matrix-vector multiplication
	public static double[] matrixProduct(double[][] mat, double[] vec) {
		int rows1 = mat.length;
		int cols1 = mat[0].length;
		int rows2 = vec.length;
		
		double[] matProduct = new double[rows2];
		
		for (int r = 0; r < rows1; ++r) {
			for (int c = 0; c < cols1; ++c) {
				matProduct[r] = matProduct[r] + mat[r][c]*vec[c];
			}
		}
		
		return matProduct;
	}
	
	// vector-matrix multiplication
	public static double[] matrixProduct(double[] vec, double[][] mat) {
		//int rows1 = mat.length;
		int cols1 = mat[0].length;
		int rows2 = vec.length;
		
		double[] matProduct = new double[rows2];
		
		for (int c = 0; c < cols1; ++c) {
			for (int r = 0; r < rows2; ++r) {
				matProduct[c] = matProduct[c] + vec[r]*mat[r][c];
			}
		}
		
		return matProduct;
	}
} 
