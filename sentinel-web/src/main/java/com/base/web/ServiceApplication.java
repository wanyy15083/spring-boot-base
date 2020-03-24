package com.base.web;

import com.alibaba.cloud.circuitbreaker.sentinel.SentinelCircuitBreakerFactory;
import com.alibaba.cloud.circuitbreaker.sentinel.SentinelConfigBuilder;
import com.alibaba.cloud.sentinel.annotation.SentinelRestTemplate;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
public class ServiceApplication {

	@Bean
	@SentinelRestTemplate(blockHandler = "handleException", blockHandlerClass = ExceptionUtil.class)
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public RestTemplate restTemplate2() {
		return new RestTemplate();
	}

	@Bean
    public Converter<String, List<FlowRule>> myParser() {
	    return new JsonFlowRuleListParser();
    }

	@Bean
	public Customizer<SentinelCircuitBreakerFactory> defaultConfig() {
		return factory -> {
			factory.configureDefault(
					id -> new SentinelConfigBuilder().resourceName(id)
							.rules(Collections.singletonList(new DegradeRule(id)
									.setGrade(RuleConstant.DEGRADE_GRADE_RT).setCount(100)
									.setTimeWindow(10)))
							.build());
		};
	}


	public static void main(String[] args) {
		SpringApplication.run(ServiceApplication.class, args);

		initFlowRule();
	}


	private static void initFlowRule() {
		List<FlowRule> rules = new ArrayList<FlowRule>();
		FlowRule rule = new FlowRule();
		rule.setResource("resource");
		// set limit qps to 10
		rule.setCount(10);
		rule.setGrade(RuleConstant.FLOW_GRADE_QPS);
		rule.setLimitApp("default");
		rules.add(rule);
		FlowRuleManager.loadRules(rules);
	}

}