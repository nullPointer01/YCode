package com.ym.ycode;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.lang.annotation.Target;

/**
 * @Author yuanming.zhang
 * @Since 2026/10/31
 **/
@Service
public class ToolService {

    @Tool(description = "执行shell命令")
    public String shellCommandTool(@ToolParam(description = "命令") String command){
        System.out.println("执行shell命令" + command);
        return "执行成功";
    }

}
