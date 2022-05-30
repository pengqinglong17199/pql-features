module commons {
    requires jdk.compiler;
    requires jdk.unsupported;

    exports kfang.agent.feature.lombok.pql.processor;
    exports kfang.agent.feature.lombok.pql.annotations;
    exports kfang.agent.feature.lombok.pql.constants;

}