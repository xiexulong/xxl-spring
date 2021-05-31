/**
 *****************************************************************************
 *                       RoadDB Confidential
 *                  Copyright (c) Continental, AG. 2018, RoadDB 2019
 *
 *      This software is furnished under license and may be used or
 *      copied only in accordance with the terms of such license.
 *****************************************************************************
 * @file   ConfigService.java
 * @brief  Config service interface
 *******************************************************************************
 */

package com.xxl.service;

import com.xxl.model.Config;

import java.util.List;
import java.util.Map;




public interface ConfigService {

    List<Config> list(String application, String item, Integer favorite);

    void update(Map<Long, String> config);

}
