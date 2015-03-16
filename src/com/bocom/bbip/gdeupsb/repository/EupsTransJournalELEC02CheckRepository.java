package com.bocom.bbip.gdeupsb.repository;

import java.util.Map;

import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.domain.Page;
import com.bocom.bbip.data.domain.Pageable;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.EupsTransJournalELEC02Check;

public interface EupsTransJournalELEC02CheckRepository extends PagingAndSortingRepository<EupsTransJournalELEC02Check, String> {
  @Find
  public Page<EupsTransJournalELEC02Check>getCheckInfo(Pageable pageable,Map<String,String>map);
}
