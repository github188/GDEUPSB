package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.GDEupsCusAgentJournal;

public interface GDEupsCusAgentJournalRepository extends PagingAndSortingRepository<GDEupsCusAgentJournal, String> {
    
    public List<Map<String, Object>> findAllGroupByComNo(Map<String, Object> map);
}