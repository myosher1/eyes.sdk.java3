package com.applitools.eyes.config;

import com.applitools.eyes.config.ISeleniumConfigurationGetter;
import com.applitools.eyes.config.ISeleniumConfigurationSetter;

public interface ISeleniumConfigurationProvider{

    ISeleniumConfigurationGetter get();

    ISeleniumConfigurationSetter set();
}
