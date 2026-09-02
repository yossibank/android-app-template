.PHONY: verify lint format build test clean

verify:
	./gradlew ktlintCheck assembleDebug testDebugUnitTest

lint:
	./gradlew ktlintCheck

format:
	./gradlew ktlintFormat

build:
	./gradlew assembleDebug

test:
	./gradlew testDebugUnitTest

clean:
	./gradlew clean
