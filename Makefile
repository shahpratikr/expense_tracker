# Makefile for the Expense Tracker Android app.
# Wraps the Gradle wrapper with the JDK 17 + truststore settings this build needs.
#
# Override any variable on the command line, e.g.:
#   make apk JAVA_HOME=/opt/jdk-17
#   make test TLS=            # disable the proxy truststore args

JAVA_HOME ?= /usr/lib/jvm/java-17-openjdk-amd64
ANDROID_HOME ?= /home/pratik/Android/Sdk

# TLS options for dependency downloads behind a TLS-inspecting proxy. Clear with `TLS=`.
TLS ?= -Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts -Djavax.net.ssl.trustStorePassword=changeit

GRADLE = JAVA_HOME=$(JAVA_HOME) ANDROID_HOME=$(ANDROID_HOME) ./gradlew
APK = app/build/outputs/apk/expense_tracker.apk

.DEFAULT_GOAL := help
.PHONY: help apk release install test lint clean

help: ## Show available targets
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | \
		awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-10s\033[0m %s\n", $$1, $$2}'

apk: ## Build the APK
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
