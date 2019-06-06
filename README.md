# Ellipse
ECC算法实现，理论来源于《SM2椭圆曲线公钥密码算法》，

关于Ellipse.java
增加privateKey,basePoint,publicKey,rankOfEllipse,allPoints等私有变量。
增加getPublicKey,setPrivateKey,getAllPointsOnEllipse,getRankOfEllipse等接口。

Inv取倒数，原功能改名为extraEuclid（扩展的欧几里得算法），增加AexpB方法（求指数）

增加square开平方功能，以及AexpB，AmodB，rankOfNum，checkRankOfNum，getProbPrime等功能，收集在Utils常用工具类中。

 * privateKey为私钥,basePoint为基点,publicKey为公钥;
 * rankOfBasePoint为基点的阶，rankOfEllipse为Ellipse的阶;
 * multiPoint(k,P)更新私钥和基点，返回公钥;
 * getAllPointsOnEllipse()返回所有平常点;
 * getRankOfBasePoint()返回基点的阶;
 * setPrivateKey(k)设置私钥;
 * setBasePoint(P)设置基点;
 * getPublicKey()返回公钥;

还有Utils.java，包括数论中常见算法，整数求模aExpB(long,long),整数求平方根square(long,long)等。
