
/* 使用しているSURFのライブラリに関して  */
http://jopensurf.googlecode.com/svn/trunk
から取得できます。
使用しているのは、リビジョン13のもので
com.stromberglabs.jopensurfパッケージのものが、それに当たります。
System.out.printlnがうざかったので、それだけコメントアウトしています。
それ以外は変更していません。
本家で更新がなされているが、どうも処理が遅くなるようなので、更新してません

また、個人的に扱いやすいように変更を加えているのが、
mySurfパッケージです。

/* jarの実行 */
/** 2つの画像の比較  *//
java -cp mySurf.jar mySurf.MySurfCompare 画像A 画像B




/* メモ */
・jarの実行
java -cp jarファイル パッケージ.クラス [引数]


