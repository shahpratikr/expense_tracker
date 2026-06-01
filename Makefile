# Makefile for the Expense Tracker Android app.
# Wraps the Gradle wrapper with the JDK 17 + truststore settings this build needs.
#
# Override any variable on the command line, e.g.:
#   make apk JAVA_HOME=/opt/jdk-17
#   make test TLS=            # disable the proxy truststore args

JAVA_HOME ?= /home/pratik/tools/jdk-17.0.19+10
ANDROID_HOME ?= /home/pratik/android-sdk

# TLS options for dependency downloads behind a TLS-inspecting proxy. Clear with `TLS=`.
TLS ?= -Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts -Djavax.net.ssl.trustStorePassword=changeit

GRADLE = JAVA_HOME=$(JAVA_HOME) ANDROID_HOME=$(ANDROID_HOME) ./gradlew
APK = app/build/outputs/apk/debug/app-debug.apk

.DEFAULT_GOAL := help
.PHONY: help apk debug release install test lint clean

help: ## Show available targets
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-10s\033[0m %s\n", $$1, $$2}'

apk: debug ## Alias for `debug`

debug: ## Build the debug APK
	$(GRADLE) assembleDebug $(TLS)
	@echo "APK: $(APK)"

release: ## Build the release APK
	$(GRADLE) assembleRelease $(TLS)

install: ## Install the debug APK on a connected device/emulator
	$(GRADLE) installDebug $(TLS)

test: ## Run all unit tests
	$(GRADLE) test $(TLS)

lint: ## Run Android lint
	$(GRADLE) lint $(TLS)

clean: ## Remove build outputs
	$(GRADLE) clean $(TLS)
