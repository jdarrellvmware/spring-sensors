package org.tanzu.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.tanzu.demo.config.WebProperties;
import org.tanzu.demo.model.Sensor;
import org.tanzu.demo.model.SensorData;
import org.tanzu.demo.model.SensorRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

@Controller
public class DemoController {

    @Autowired
    JdbcTemplate _jdbcTemplate;

    @Autowired
    SensorRepository _sensorRepository;

    @Autowired
    WebProperties _webProperties;

    @RequestMapping("/")
    public String home(Model model) throws SQLException {
        Connection connection = Objects.requireNonNull(_jdbcTemplate.getDataSource()).getConnection();
        model.addAttribute("sensorDB", "Sensor DB: " + connection.getMetaData().getURL());
        connection.close();
        return "index";
    }

    @RequestMapping("/write")
    public @ResponseBody
    Map<String, Object> write() {
        _sensorRepository.save(new Sensor(new Random().nextInt(120) + 170, new Random().nextInt(25000) + 15000));
        return new HashMap<>();
    }

    private static final String REPLACEMENT_BANNER_TEXT="Tanzu Application Platform Demo";

    @RequestMapping("/refresh")
    public @ResponseBody SensorData refresh() {
        SensorData result = new SensorData( _sensorRepository.findAll(), _webProperties.getTempHeader(),
                _webProperties.getPressureHeader(), _webProperties.getBannerTextColor(), _webProperties.getBannerText());
        return result;
    }
}

