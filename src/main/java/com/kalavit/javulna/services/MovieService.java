/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kalavit.javulna.services;

import com.kalavit.javulna.dto.MovieDto;
import com.kalavit.javulna.model.Movie;
import com.kalavit.javulna.services.autodao.MovieAutoDao;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author peti
 */
@Service
public class MovieService {
    
    private static final Logger LOG = LoggerFactory.getLogger(MovieService.class);
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    MovieAutoDao movieAutoDao;
    
    public List<MovieDto> findMovie(String title, String description, String genre, String id) {
        int conditions = 0;
        StringBuilder sql = new StringBuilder("select description, title, genre, id from movie ");
        Map<Integer, String> argMap = new HashMap<>();
        if (StringUtils.hasText(title)) {
            appendCondition(sql, conditions);
            conditions++;
            sql.append("title LIKE '%?%'");
            argMap.put(conditions, title);
        }
        if (StringUtils.hasText(description)) {
            appendCondition(sql, conditions);
            conditions++;
            sql.append("description LIKE '%?%'");
            argMap.put(conditions, description);
        }
        if (StringUtils.hasText(genre)) {
            appendCondition(sql, conditions);
            conditions++;
            sql.append("genre LIKE '%?%'");
            argMap.put(conditions, genre);
        }
        if (StringUtils.hasText(id)) {
            appendCondition(sql, conditions);
            conditions++;
            sql.append("id ='?'");
            argMap.put(conditions, id);
        }
        LOG.debug(sql.toString());
        return jdbcTemplate.execute((StatementCallback<List<MovieDto>>) stmt -> {
            PreparedStatement prepareStatement = stmt.getConnection().prepareStatement(stmt.toString());
            argMap.forEach((k,v)-> {
                try {
                    prepareStatement.setString(k,v);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });
            ResultSet rs = prepareStatement.executeQuery();
            List<MovieDto> movieDtoList = new ArrayList<>();
            while (rs.next()) {
                MovieDto movieDto = new MovieDto();
                movieDto.setDescription(rs.getString("description"));
                movieDto.setTitle(rs.getString("title"));
                movieDto.setGenre(rs.getString("genre"));
                movieDto.setId(rs.getString("id"));
                movieDtoList.add(movieDto);
            }
            return movieDtoList;
        });
    }

    private void appendCondition(StringBuilder sb, int conditions) {
        if (conditions == 0) {
            sb.append(" where ");
        } else {
            sb.append(" and ");
        }
    }
    
    public Movie saveMovieFromXml(String xml){
        try {
            Movie m = new Movie();
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            DocumentBuilder db = factory.newDocumentBuilder();
            Document doc = db.parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
            Element root = doc.getDocumentElement();
            m.setTitle(getText(root, "title"));
            m.setDescription(getText(root, "description"));
            m.setGenre(getText(root, "genre"));
            movieAutoDao.save(m);
            return m;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        } 
    }

    private String getText(Element el, String tagName) {
        NodeList nl = el.getElementsByTagName(tagName);
        if(nl.getLength() >0){
            NodeList children = nl.item(0).getChildNodes();
            if(children.getLength() > 0){
                return children.item(0).getTextContent();
            }
        }
        LOG.debug("no text content of tag with name: {}", tagName);
        return null;
    }
    
}
