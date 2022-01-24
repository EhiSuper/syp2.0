package it.unipi.dii.inginf.lsdb.syp.rule;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class RuleController {
    private final RuleService ruleService;

    RuleController(RuleService ruleService) {
        this.ruleService = ruleService;
    }

    @GetMapping("/api/songs/findRules")
    void mineRules(){
        ruleService.startMining();
    }

}
