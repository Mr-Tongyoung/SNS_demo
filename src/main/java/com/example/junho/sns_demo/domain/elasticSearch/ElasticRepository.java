package com.example.junho.sns_demo.domain.elasticSearch;

import java.util.List;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ElasticRepository extends
    ElasticsearchRepository<PostDocument, String> {
  List<PostDocument> findByContentContaining(String keyword);
}
