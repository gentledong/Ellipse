package cn.com.shfe.sfit.dong;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: Ellipse
 * @Description:针对于加密的椭圆曲线，Eq(a,b)由a,b,q三个参数决定
 * privateKey为私钥,basePoint为基点,publicKey为公钥;
 * rankOfBasePoint为基点的阶，rankOfEllipse为Ellipse的阶;
 * multiPoint(k,P)更新私钥和基点，返回公钥;
 * getAllPointsOnEllipse()返回所有平常点;
 * getRankOfBasePoint()返回基点的阶;
 * setPrivateKey(k)设置私钥;
 * setBasePoint(P)设置基点;
 * getPublicKey()返回公钥;
 * @author: dong.yanchao
 * @date: 2018年8月14日 10点42分
 * @Copyright: v2
 */
public class Ellipse {
	//Eq(a,b)，椭圆曲线的三个参数
	private int a;
	private int b;
	private int q;

	//私钥
	private long privateKey;
	//基点
	private String basePoint;
	//公钥
	private String publicKey;
	
	//基点的阶
	private long rankOfBasePoint;
	//Ellipse的阶
	private long rankOfEllipse;
	//Ellipse上的所有平常点
	private List<String> allPoints;
	
	public Ellipse(int a, int b, int q) {
		this.a = a;
		this.b = b;
		this.q = q;
		this.allPoints = this.getAllPointsOnEllipse();
		this.rankOfEllipse = this.allPoints.size() + 1;
	}
	public int getA() {
		return a;
	}
	public void setA(int a) {
		this.a = a;
	}
	public int getB() {
		return b;
	}
	public void setB(int b) {
		this.b = b;
	}
	public int getQ() {
		return q;
	}
	public void setQ(int q) {
		this.q = q;
	}
	//返回私钥
	public long getPrivateKey() {
		return privateKey;
	}
	//重置私钥
	public void setPrivateKey(long k) {
		this.privateKey = k;
	}
	//返回基点
	public String getBasePoint() {
		return basePoint;
	}
	//重置基点
	public void setBasePoint(String p) {
		basePoint = p;
		this.rankOfBasePoint = getRankOfBasePoint();
	}
	//返回公钥
	public String getPublicKey() {
		return publicKey;
	}
	//返回Ellipse的阶
	public long getRankOfEllipse() {
		return rankOfEllipse;
	}
	//返回所有Ellipse的平常点，不包括无穷远点
	public List<String> getAllPoints() {
		return allPoints;
	}
	public static void main(String[] args) {
//		Ellipse algorithm = new Ellipse(1,1,23);//E23(1,1)
//		String s = "3v10";//基点G(3,10)
//		for (int j = 0; j < 100; j++) {
//			String multiPoint = algorithm.multiPoint(j+1, s);
//			System.out.println(j+1+":"+multiPoint);
//			if(multiPoint.equals("0v0"))break;
//		}
		
//		Ellipse algorithm = new Ellipse(1,1,19);//E19(1,1)
//		String s = "10v2";//基点G(10,2)
//		String multiPoint = algorithm.multiPoint(2,s);
//		System.out.println(multiPoint);

//		List<String> allPointsOnEllipse = algorithm.getAllPointsOnEllipse();
//		for (String string : allPointsOnEllipse) {
//			System.out.println(string);
//		}
		Ellipse algorithm = new Ellipse(1,1,23);//Eq(a,b)
		System.out.println(algorithm.getAllPoints());//所有平常点
		System.out.println(algorithm.getRankOfEllipse());//Ellipse的阶
		algorithm.setPrivateKey(8);//设置私钥
		algorithm.setBasePoint("3v10");//设置基点
		System.out.println(algorithm.getRankOfBasePoint());//基点的阶
		algorithm.multiPoint(algorithm.getPrivateKey(), algorithm.getBasePoint());
		System.out.println(algorithm.getPublicKey());//返回公钥
		
	}
	
	/**
	 * @Title: multiPoint
	 * @Description: 
	 * 倍点运算，给定一个点P，按照k的二进制展开进行计算
	 * @return: String，"XvY"形式,表示点(x,y)
	 */
	public String multiPoint(long k,String P) {
		List<Byte> bins = new ArrayList<Byte>();//k的二进制展开
		if (k == 0)return "0v0"; 
		while (k != 0) {
			byte tmp = (byte) (k % 2);
			bins.add(tmp);
			k /= 2;
		}
		String Q = "0v0";
		for (int i = bins.size() - 1; i >= 0; i--) {
			Q = doublePoint(Q);//Q=2Q
			if (bins.get(i) == 1)Q = pAddP(Q, P);//Q=Q+P
		}
		
		this.publicKey = Q;
		return Q;
	}
	
	/**
	 * @Title: doublePoint
	 * @Description: 计算一个点p的2倍
	 * @param: p 被计算的点
	 * @return: String，"XvY"形式,表示点(x,y)
	 */
	public String doublePoint(String p) {
		String[] vp = p.split("v");
		int x1 = Integer.parseInt(vp[0]);
		int y1 = Integer.parseInt(vp[1]);
		if (y1 == 0)return "0v0";//分母为0
		
		long numer = 3 * x1 * x1 + a;//分子
		long denom = 2 * y1;//分母
		denom = inv(denom, q);//计算分母的逆元
		denom = AmodB(denom,q);
		long slope = (long)AmodB(numer*denom,q);
		long x11 = AmodB(slope*slope-2*x1,q);
		int Xr = (int) x11;// 横坐标
		long y11 = AmodB(slope*(x1-Xr)-y1,q);
		int Yr = (int) y11;// 纵坐标
		return Xr + "v" + Yr;// 返回2倍点的新坐标
	}

	/**
	 * @Title: pAddP
	 * @Description: 计算两个不同点相加
	 * @param: P1
	 * @param: P2
	 * @return: String，"XvY"形式,表示点(x,y)
	 */
	public String pAddP(String P1, String P2) {
		if (P2.equals(P1)) return doublePoint(P1);// 如果两个点一致，直接调用doublePoint运算
		
		String[] vp = P1.split("v");
		String[] vq = P2.split("v");
		if (vp[0].equals(vq[0]))return "0v0";//x1==x2，不可能

		if (P2.equals("0v0"))return P1;
		if (P1.equals("0v0"))return P2;
		//P1和P2全不为0v0，则展开如下计算
		int x1 = Integer.parseInt(vp[0]);
		int y1 = Integer.parseInt(vp[1]);

		int x2 = Integer.parseInt(vq[0]);
		int y2 = Integer.parseInt(vq[1]);

		long DValueY = AmodB(y2-y1,q);
		long DValueX = AmodB(x2-x1,q);
		DValueX = inv(DValueX, q);//取倒数
		DValueX = AmodB(DValueX,q);
		long slope = AmodB(DValueY*DValueX,q);//斜率
		long xrr = AmodB(slope*slope-x1-x2,q);
		
		int xr = (int) xrr;
		long y11 = AmodB(slope*(x1-xr)-y1,q);
		int yr = (int) y11;
		return xr + "v" + yr;
	}
	/**
	 * @Title: inv 取倒数
	 * @Description: 计算逆元，理解为在mod r2的基础上，“取倒数”的意思
	 * 假设得到的结果为res，则有res*r1 == 1 （mod r2）恒成立
	 */
	public long inv(long r1,long r2){
		return extraEuclid(r1,r2,1,0);
//		return AexpB(r1,r2-2);
	}
	/**
	 * @Title: extraEuclid 扩展的欧几里得算法
	 */
	public long extraEuclid(long r1, long r2, long x1, long x2) {
		long qr = r1 / r2;
		long r = r1 % r2;
		long x = x1 - qr * x2;
		if (r == 1) return x;
		return extraEuclid(r2, r, x2, x);
	}
	/**
	 * @Title: AexpB Fq上的指数运算
	 * @Description: 计算a^b的值
	 * @return: long ，返回值范围[0,b-1]
	 */
	public long AexpB(long g,long e){
		if(AmodB(e,q)==0)return 1l;
		List<Byte> bins = new ArrayList<Byte>();//e的二进制展开
		while (e != 0) {
			byte tmp = (byte) (e % 2);
			bins.add(tmp);
			e /= 2;
		}
		long x = g; 
		for (int i = bins.size() - 2; i >= 0; i--) {
			x = AmodB(x*x,q);//x=x^2
			if (bins.get(i) == 1)x = AmodB(g*x,q);//x=g*x
		}
		return x;
	}
	/**
	 * @Title: AmodB 整数求模运算
	 * @Description: 计算a mod b的值
	 * @return: long ，返回值范围[0,b-1]
	 */
	public long AmodB(long a, long b) {
		long res = a % b;
		if(res<0)return res+b;
		return res; 
	}
	
	/**
	 * @Title: getAllPointsOnEllipse 求椭圆曲线上所有的平常点，不包括无穷远点
	 * @Description: 平常点(X,Y)，X和Y都属于Fp有限域，(X,Y)满足Eq(a,b)方程
	 * @return: List<String>，点的集合，每个String采用"XvY"的格式，即平常点(X,Y)
	 */
	public List<String> getAllPointsOnEllipse(){
		List<String> retPoints = new ArrayList<String>();
		for(long X = 0;X<q;X++){
			long A = AmodB(X*X*X+a*X+b,q);
			if(A==0){
				retPoints.add(new String(X+"v0"));
				continue;
			}
			long Y = square(A);
			if(q==Y)continue;
			retPoints.add(new String(X+"v"+Y));
			retPoints.add(new String(X+"v"+(q-Y)));
		}
		return retPoints;
 	}
	/**
	 * @Title: square 整数求平方根的运算
	 * @param g 满足[0,q)的整数
	 * @Description: ret为g的平方根，有ret*ret=g（mod q）恒成立，若ret成立，则q-ret也成立
	 * @return: long ，返回任意一个值，返回值范围[0,q-1]，另一个为q-ret;返回q，则代表g不存在平方根
	 */
	public long square(long g){
		if(g<0||g>=q){
			System.err.print("square的参数g应满足[0,q)");
			System.exit(0);
		}
		if(g==0)return 0;
		long u = q/4;
		long ret = AexpB(g,u+1);
		if(AmodB(ret*ret,q)==g)return ret;
		return q;
	}
	/**
	 * @Title: getRankOfBasePoint 求基点的阶
	 * @Description: 设P为基点，nP=O，则n为P的阶。有限域上P的阶一定存在，可证明
	 */
	public long getRankOfBasePoint(){
		long ret = -1L;
		String G = this.basePoint;
		for(long i = 2;;i++){
			G=pAddP(G,this.basePoint);
			if(G.equals("0v0")){
				ret = i;
				break;
			}
		}
		return ret;
	}
	
}

