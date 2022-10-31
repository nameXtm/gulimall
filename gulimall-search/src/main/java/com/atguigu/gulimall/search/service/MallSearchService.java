package com.atguigu.gulimall.search.service;

import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResult;

import java.io.IOException;

public interface MallSearchService {
    SearchResult search(SearchParam searchParam) throws IOException;
}
