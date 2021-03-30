def call(String subscription, Closure block) {
  withDocker('hmcts/cnp-aks-client:az-2.21.0-hotfix1-kubectl-1.20.5-helm-3.5.3', null) {
    withSubscriptionLogin(subscription) {
      withRegistrySecrets {
        block.call()
      }
    }
  }
}


