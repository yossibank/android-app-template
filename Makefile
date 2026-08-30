.PHONY: verify lint format build test clean

# 変更後に必ず通すもの。
verify:
	./gradlew ktlintCheck assembleDebug testDebugUnitTest

lint:
	./gradlew ktlintCheck

# 自動修正できるものを直す。
format:
	./gradlew ktlintFormat

build:
	./gradlew assembleDebug

test:
	./gradlew testDebugUnitTest

clean:
	./gradlew clean
