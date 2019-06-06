package cn.com.shfe.sfit.dong;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: Utils
 * @Description:常用功能，数论中的某些算法，均为static函数形式;
 * @author: dong.yanchao
 * @date: 2018年8月14日 10点42分
 * @Copyright: v1
 */
public class Utils {
	public static void main(String[] args) {
		long g = 6;
		long q = 23;
		long rankOfNum = Utils.rankOfNum(g, q);
		System.out.println(rankOfNum);
		long aexpB = Utils.AexpB(g,rankOfNum,q);
		System.out.println(aexpB+" "+AmodB(aexpB,q));
		System.out.println(Utils.checkRankOfNum(g, rankOfNum, q));
		
		/*boolean flag = false;
		for(int i = 1;i<100;i++){
			if(Utils.checkProbPrime(17, 10L)==true){
				System.out.println("true");//17素数,93合数,293素数,41素数
				flag = true;
				break;
			}
		}
		if(flag==false)System.out.println(flag);*/
		
		
	}
	/**
	 * @Title: AexpB Fq上的指数运算
	 * @param q，Eq(a,b)中的素数q
	 * @Description: 计算a^b的值
	 * @return: long ，返回值范围[0,b-1]
	 */
	public static long AexpB(long g,long e,long q){
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
	public static long AmodB(long a, long b) {
		long res = a % b;
		if(res<0)return res+b;
		return res; 
	}
	/**
	 * @Title: square 整数求平方根的运算
	 * @param g 满足[0,q)的整数
	 * @param q Eq(a,b)中的素数q
	 * @Description: ret为g的平方根，有ret*ret=g（mod q）恒成立，若ret成立，则q-ret也成立
	 * @return: long ，返回任意一个值，返回值范围[0,q-1]，另一个为q-ret;返回q，则代表g不存在平方根
	 */
	public static long square(long g,long q){
		if(g<0||g>=q){
			System.err.print("square的参数g应满足[0,q)");
			System.exit(0);
		}
		if(g==0)return 0;
		long u = q/4;
		long ret = AexpB(g,u+1,q);
		if(AmodB(ret*ret,q)==g)return ret;
		return q;
	}
	/**
	 * @Title: rankOfNum 确定g(mod p)的阶
	 * @param g 整数 范围在[2,q) 
	 * @param q是Eq(a,b)中的素数q
	 * @Description: 约定返回值为k，满足g^k=1(mod q)恒成立，并且k是最小的正整数 
	 */
	public static long rankOfNum(long g,long q){
		long b = g,j = 1;
		do{
			b = AmodB(g*b,q);
			j++;
		}while(b>1);
		return j;
	}
	/**
	 * @Title: checkRankOfNum 验证k为确定g(mod p)的阶
	 * @param g 整数 范围在[2,q) 
	 * @param q是Eq(a,b)中的素数q
	 * @return k是g(mod p)的阶，返回true，否则返回false
	 * @Description: 约定返回值为k，满足g^k=1(mod q)恒成立，并且k是最小的正整数 
	 */
	public static boolean checkRankOfNum(long g,long k,long q){
		if(AmodB(AexpB(g,k,q),q)!=1)return false;
		List<Long> primesFactor = getPrimesFactorList(k);
		for (Long factor : primesFactor) {
			if(AmodB(AexpB(g,factor,q),q)==1)return false;
		}
		return true;
	}
	/**
	 * @Title: getPrimesFactorList(k) 返回素k的素数因子
	 * @param k ，被要求素因子的数  
	 * @return List，包括k的所有素因子，不包括1和k本身
	 */
	public static List<Long> getPrimesFactorList(long k) {
		List<Long> primesList = getPrimesList(k);
		List<Long> retList = new ArrayList<Long>();
		for (Long prime : primesList) {
			if(k%prime==0)retList.add(prime);
		}
		return retList;
	}
	/**
	 * @Title: getPrimesList 返回素数List
	 * @param N long，素数区间的右边界  
	 * @return [2,N)之间的素数，不包括N
	 */
 	public static List<Long> getPrimesList(long N) {
		List<Boolean> checks = new ArrayList<Boolean>();
		for(int i = 1;i<=N;i++)checks.add(false);
		List<Long> primes = new ArrayList<Long>();
		for (int i = 2; i <= N - 1; ++i) {
			if (checks.get(i) == true)continue;
			primes.add((long) i);
			for (int j = i*i; j <= N - 1; j += i) checks.set(j,true);
		}
		return primes;
	}
 	
 	/**
	 * @Title: checkProbPrime(u,T) 检查u是否为概率素数，u不为素数的概率小于2^(-2T)
	 * @Description u是合数的概率小于2^(-2T)，只要T足够大，误差可以忽略
	 * @return boolean，u为T下的概率素数，返回true，否则返回false
	 */
 	public static boolean checkProbPrime(long u, long T){
 		if(u==2)return true;
 		if(u%2==0)return false;
 		long v = -1,w = -1;
 		for(int i = 1;;i++){
 			long pow = (long) Math.pow(2.0,i*1.0);
 			w = (u-1)/pow;
 			if(w%2==0)continue;
 			v = (long)i;
 			break;
 		}
 		if(v==-1){
 			System.err.println("u-1=2^v*w没有成功");
 			System.exit(0);
 		}
 		for(int j = 1;j<=T;j++){
			long a = (long)(Math.random()*(u-2)+2+1);
			long b = (long) (Math.pow(a, w))%u;
			if(b==1||b==u-1)continue;
			for(int i = 1;i<=v-1;i++){
				b=b*b%u;
				if(b==u-1)break;
				if(b==1)return false;
			}
			if(b==u-1)continue;
			return false;
 		}
 		return true;
 	}
}
