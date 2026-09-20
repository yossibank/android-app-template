GRADLE := ./gradlew

# CI は増分を当てにしない。UP-TO-DATE / FROM-CACHE で素通りすると、
# 警告もテスト結果も出ないまま green になる。
ifdef CI
GRADLE_FLAGS := --rerun-tasks
endif

.PHONY: verify lint format build test clean

verify:
	$(GRADLE) ktlintCheck assembleDebug testDebugUnitTest $(GRADLE_FLAGS)

lint:
	$(GRADLE) ktlintCheck $(GRADLE_FLAGS)

format:
	$(GRADLE) ktlintFormat

build:
	$(GRADLE) assembleDebug $(GRADLE_FLAGS)

test:
	$(GRADLE) testDebugUnitTest $(GRADLE_FLAGS)

clean:
	$(GRADLE) clean
