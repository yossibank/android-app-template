KMP_DIR ?= ../kmp-app-template

.PHONY: bootstrap verify build test clean

# 共通コアを mavenLocal へ publish する。clone 直後と kmp 変更後に必要。
bootstrap:
	$(MAKE) -C $(KMP_DIR) publish-local

# 変更後に必ず通すもの。
verify:
	./gradlew assembleDebug testDebugUnitTest

build:
	./gradlew assembleDebug

test:
	./gradlew testDebugUnitTest

clean:
	./gradlew clean
