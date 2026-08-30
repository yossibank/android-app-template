.PHONY: verify build test clean

# 変更後に必ず通すもの。
verify:
	./gradlew assembleDebug testDebugUnitTest

build:
	./gradlew assembleDebug

test:
	./gradlew testDebugUnitTest

clean:
	./gradlew clean
