package com.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: springboot-tdengine
 * @ClassName OpcServer
 * @author: ayetony miao
 * @create: 2023-08-07 21:14
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpcServer {

    private Integer id;
    private String addr;
    private Integer port;

}
