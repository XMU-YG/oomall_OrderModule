## modified by luxun@2020

## 将文件结尾从CRLF改为LF，解决了cd 错误问题
cd /home/mybaby/privilege/public-test

## 禁用了下面两行的git、mvn命令，推荐在项目第一次部署到服务器时运行git、mvn clean进行初始化
git pull
mvn clean

cd /home/mybaby/privilege/core
mvn clean install -Dmaven.test.skip=true

cd /home/mybaby/test
if [ ! -d $1  ];then
  echo "mkdir$1....."
  mkdir "$1"
else
  echo "rm dir $1....."
  rm -r "$1"
  mkdir "$1"
fi

cp -rf /home/mybaby/privilege/public-test/* $1/

cd $1
sed -i "s/\${ooad.group}/$1/g" pom.xml
sed -i "s/\${ooad.testdir}/$2/g" pom.xml
## 在主项目下使用 -pl -am 编译子项目，否则找不到依赖
## $1 代表第一个参数，$0代表命令名
mvn surefire-report:report -Dmanagement.gate=$3 -Dmall.gate=$4
mvn site:deploy
