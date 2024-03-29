package com.example.sweater.repos;

import com.example.sweater.domain.Message;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MessageRepo extends CrudRepository<Message, Long> {

    List<Message> findAll();

    List<Message> findByTagOrderByIdDesc(String tag);

    List<Message> findByTag(String tag);

    List<Message> findAllByOrderByIdDesc();


}


