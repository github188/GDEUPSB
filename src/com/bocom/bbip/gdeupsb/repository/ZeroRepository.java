package com.bocom.bbip.gdeupsb.repository;



import java.util.List;

import com.bocom.bbip.data.annotation.Delete;
import com.bocom.bbip.data.annotation.Find;
import com.bocom.bbip.data.annotation.Insert;
import com.bocom.bbip.data.annotation.Update;
import com.bocom.bbip.data.repository.PagingAndSortingRepository;
import com.bocom.bbip.gdeupsb.entity.Zero;


public interface ZeroRepository extends PagingAndSortingRepository<Zero, String>  {
	
    @Find
    public List<Zero>findZero(Zero zero);
    @Delete
    public void deleteZero(Zero zero);
    @Insert
    public void addZero(Zero zero);
    @Update
    public void updateZero(Zero zero);
    @Find
    public List<Integer>findZeroCount();
}