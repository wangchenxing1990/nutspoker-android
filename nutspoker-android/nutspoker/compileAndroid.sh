assembleType = 'assembleDebug'

./gradlew assembleType

IN_PATH=`pwd`

if [ $# -le 0 ]; then

else
	scanFile $1
fi