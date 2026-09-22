GRADLE := ./gradlew

ifdef CI
GRADLE_FLAGS := --rerun-tasks
endif

.PHONY: verify verify-debug lint format build release test clean

verify:
	$(GRADLE) ktlintCheck assembleDebug testDebugUnitTest :core:test assembleRelease $(GRADLE_FLAGS)

verify-debug:
	$(GRADLE) ktlintCheck assembleDebug testDebugUnitTest :core:test $(GRADLE_FLAGS)

lint:
	$(GRADLE) ktlintCheck $(GRADLE_FLAGS)

format:
	$(GRADLE) ktlintFormat

build:
	$(GRADLE) assembleDebug $(GRADLE_FLAGS)

release:
	$(GRADLE) assembleRelease $(GRADLE_FLAGS)

test:
	$(GRADLE) testDebugUnitTest :core:test $(GRADLE_FLAGS)

clean:
	$(GRADLE) clean
