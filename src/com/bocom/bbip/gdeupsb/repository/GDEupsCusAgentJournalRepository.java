package com.bocom.bbip.gdeupsb.repository;

import java.util.List;
import java.util.Map;

import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.eups.entity.EupsCusAgentJournal;

public interface GDEupsCusAgentJournalRepository extends PagingAndSortingRepository<EupsCusAgentJournal, String> {
    
    public List<Map<String, Object>> findAllGroupByComNo(Map<String, Object> map);
}