package dev.shounakmulay.devpulse.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.RuleSet
import io.gitlab.arturbosch.detekt.api.RuleSetProvider

class DevPulseRuleSetProvider : RuleSetProvider {
    override val ruleSetId: String = "devpulse"

    override fun instance(config: Config): RuleSet = RuleSet(
        ruleSetId,
        listOf(NavigationSerializerRegistration(config)),
    )
}
