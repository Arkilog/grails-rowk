package grails.plugins.rowk

import grails.plugins.rowk.types.*

abstract class RowkValue {

	abstract val()
    String toString() {"${val()}"}
    static mainTypes = [
		(Boolean.class) : RowkBooleanValue,
    	(String.class) : RowkStringValue,
    	(Date.class) : RowkDateValue,
    	(Integer.class) : RowkIntegerValue,
    	(Long.class) : RowkLongValue,
    	(Short.class) : RowkShortValue,
    	(Float.class) : RowkFloatValue,
    	(BigDecimal.class) : RowkBigDecimalValue,
    	(List.class) : RowkListValue,
    	(Map.class) : RowkMapValue
    ]
    static RowkValue create(val){
    	def rowkValue = null
    	def rowkClass = mainTypes.get(val?.'class')
    	if (rowkClass){
    		rowkValue = rowkClass.newInstance()
    		rowkValue.value = val
    	} else {
    		if (val.id){
    			rowkValue = new RowkDomainValue()
    			rowkValue.value = val
    		}
    	}
    	return rowkValue
    }
}
