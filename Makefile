GRADLE := ./gradlew

.PHONY: verify verify-debug lint format build release test clean token

verify:
	$(GRADLE) ktlintCheck assembleDebug testDebugUnitTest assembleRelease

verify-debug:
	$(GRADLE) ktlintCheck assembleDebug testDebugUnitTest

lint:
	$(GRADLE) ktlintCheck

format:
	$(GRADLE) ktlintFormat

build:
	$(GRADLE) assembleDebug

release:
	$(GRADLE) assembleRelease

test:
	$(GRADLE) testDebugUnitTest

clean:
	$(GRADLE) clean

token:
	@sh scripts/codeartifact-token.sh
