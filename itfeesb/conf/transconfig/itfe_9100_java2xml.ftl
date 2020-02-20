<?xml version="1.0" encoding="GBK"?>
<CFX>
	<HEAD>
		<VER>${cfx.HEAD.VER}</VER>
		<SRC>${cfx.HEAD.SRC}</SRC>
		<DES>${cfx.HEAD.DES}</DES>
		<APP>${cfx.HEAD.APP}</APP>
		<MsgNo>${cfx.HEAD.MsgNo}</MsgNo>
		<MsgID>${cfx.HEAD.MsgID}</MsgID>
		<MsgRef>${cfx.HEAD.MsgRef}</MsgRef>
		<WorkDate>${cfx.HEAD.WorkDate}</WorkDate>
	</HEAD>
	<MSG>
		<BatchHead9100>
			<UpdateBatch>${cfx.MSG.BatchHead9100.UpdateBatch}</UpdateBatch>
		</BatchHead9100>
		<TaxCodeInfo9100>
			<DataType>${cfx.MSG.TaxCodeInfo9100.DataType}</DataType>
			<UpdateNum>${cfx.MSG.TaxCodeInfo9100.UpdateNum}</UpdateNum>
			<#list cfx.MSG.TaxCodeInfo9100.TaxCodeInfo101 as var1>
			<TaxCodeInfo101>
				<TaxOrgCode>${var1.TaxOrgCode}</TaxOrgCode>
				<TaxOrgName>${var1.TaxOrgName}</TaxOrgName>
				<TaxOrgType>${var1.TaxOrgType}</TaxOrgType>
				<OrgLevel>${var1.OrgLevel}</OrgLevel>
				<UpTreCode>${var1.UpTreCode}</UpTreCode>
				<OfNodeCode>${var1.OfNodeCode}</OfNodeCode>
				<OfProvOrg>${var1.OfProvOrg}</OfProvOrg>
				<OfCityOrg>${var1.OfCityOrg}</OfCityOrg>
				<OfCountyOrg>${var1.OfCountyOrg}</OfCountyOrg>
				<Address>${var1.Address}</Address>
				<PostalCode>${var1.PostalCode}</PostalCode>
				<PeopleName>${var1.PeopleName}</PeopleName>
				<PeoplePhone>${var1.PeoplePhone}</PeoplePhone>
				<PeopleMobile>${var1.PeopleMobile}</PeopleMobile>
				<Email>${var1.Email}</Email>
				<OperSign>${var1.OperSign}</OperSign>
				<EffectDate>${var1.EffectDate}</EffectDate>
				<UpdateArea>${var1.UpdateArea}</UpdateArea>
			</TaxCodeInfo101>
			</#list>
		</TaxCodeInfo9100>
		<BankCodeInfo9100>
			<DataType>${cfx.MSG.BankCodeInfo9100.DataType}</DataType>
			<UpdateNum>${cfx.MSG.BankCodeInfo9100.UpdateNum}</UpdateNum>
			<#list cfx.MSG.BankCodeInfo9100.BankCodeInfo102 as var2>
			<BankCodeInfo102>
				<ReckBankNo>${var2.ReckBankNo}</ReckBankNo>
				<GenBankName>${var2.GenBankName}</GenBankName>
				<ReckonType>${var2.ReckonType}</ReckonType>
				<OfNodeCode>${var2.OfNodeCode}</OfNodeCode>
				<Address>${var2.Address}</Address>
				<PostalCode>${var2.PostalCode}</PostalCode>
				<PeopleName>${var2.PeopleName}</PeopleName>
				<PeoplePhone>${var2.PeoplePhone}</PeoplePhone>
				<PeopleMobile>${var2.PeopleMobile}</PeopleMobile>
				<Email>${var2.Email}</Email>
				<OperSign>${var2.OperSign}</OperSign>
				<EffectDate>${var2.EffectDate}</EffectDate>
				<UpdateArea>${var2.UpdateArea}</UpdateArea>
			</BankCodeInfo102>
			</#list>
		</BankCodeInfo9100>
		<NodeCodeInfo9100>
			<DataType>${cfx.MSG.NodeCodeInfo9100.DataType}</DataType>
			<UpdateNum>${cfx.MSG.NodeCodeInfo9100.UpdateNum}</UpdateNum>
			<#list cfx.MSG.NodeCodeInfo9100.NodeCodeInfo103 as var3>
			<NodeCodeInfo103>
				<NodeCode>${var3.NodeCode}</NodeCode>
				<NodeName>${var3.NodeName}</NodeName>
				<OfNodeType>${var3.OfNodeType}</OfNodeType>
				<NodeDN>${var3.NodeDN}</NodeDN>
				<OperSign>${var3.OperSign}</OperSign>
				<EffectDate>${var3.EffectDate}</EffectDate>
				<UpdateArea>${var3.UpdateArea}</UpdateArea>
			</NodeCodeInfo103>
			</#list>
		</NodeCodeInfo9100>
		<TreCodeInfo9100>
			<DataType>${cfx.MSG.TreCodeInfo9100.DataType}</DataType>
			<UpdateNum>${cfx.MSG.TreCodeInfo9100.UpdateNum}</UpdateNum>
			<#list cfx.MSG.TreCodeInfo9100.TreCodeInfo104 as var4>
			<TreCodeInfo104>
				<TreCode>${var4.TreCode}</TreCode>
				<TreName>${var4.TreName}</TreName>
				<TreLevel>${var4.TreLevel}</TreLevel>
				<PayBankNo>${var4.PayBankNo}</PayBankNo>
				<ReckonTreCode>${var4.ReckonTreCode}</ReckonTreCode>
				<UpTreCode>${var4.UpTreCode}</UpTreCode>
				<OfProvTrea>${var4.OfProvTrea}</OfProvTrea>
				<OfCityTrea>${var4.OfCityTrea}</OfCityTrea>
				<ofCountyTrea>${var4.ofCountyTrea}</ofCountyTrea>
				<OfNodeCode>${var4.OfNodeCode}</OfNodeCode>
				<Address>${var4.Address}</Address>
				<PostalCode>${var4.PostalCode}</PostalCode>
				<PeopleName>${var4.PeopleName}</PeopleName>
				<PeoplePhone>${var4.PeoplePhone}</PeoplePhone>
				<PeopleMobile>${var4.PeopleMobile}</PeopleMobile>
				<Email>${var4.Email}</Email>
				<OperSign>${var4.OperSign}</OperSign>
				<EffectDate>${var4.EffectDate}</EffectDate>
				<UpdateArea>${var4.UpdateArea}</UpdateArea>
			</TreCodeInfo104>
			</#list>
		</TreCodeInfo9100>
		<BudgetSubjectCodeInfo9100>
			<DataType>${cfx.MSG.BudgetSubjectCodeInfo9100.DataType}</DataType>
			<UpdateNum>${cfx.MSG.BudgetSubjectCodeInfo9100.UpdateNum}</UpdateNum>
			<#list cfx.MSG.BudgetSubjectCodeInfo9100.BudgetSubjectCodeInfo105 as var5>
			<BudgetSubjectCodeInfo105>
				<BudgetSubjectCode>${var5.BudgetSubjectCode}</BudgetSubjectCode>
				<BudgetSubjectName>${var5.BudgetSubjectName}</BudgetSubjectName>
				<SubjectType>${var5.SubjectType}</SubjectType>
				<IESign>${var5.IESign}</IESign>
				<BudgetAttrib>${var5.BudgetAttrib}</BudgetAttrib>
				<OperSign>${var5.OperSign}</OperSign>
				<EffectDate>${var5.EffectDate}</EffectDate>
				<UpdateArea>${var5.UpdateArea}</UpdateArea>
			</BudgetSubjectCodeInfo105>
			</#list>
		</BudgetSubjectCodeInfo9100>
		<ReturnReasonCodeInfo9100>
			<DataType>${cfx.MSG.ReturnReasonCodeInfo9100.DataType}</DataType>
			<UpdateNum>${cfx.MSG.ReturnReasonCodeInfo9100.UpdateNum}</UpdateNum>
			<#list cfx.MSG.ReturnReasonCodeInfo9100.ReturnReasonCodeInfo106 as var6>
			<ReturnReasonCodeInfo106>
				<ReasonCode>${var6.ReasonCode}</ReasonCode>
				<Description>${var6.Description}</Description>
				<OperSign>${var6.OperSign}</OperSign>
				<EffectDate>${var6.EffectDate}</EffectDate>
				<UpdateArea>${var6.UpdateArea}</UpdateArea>
			</ReturnReasonCodeInfo106>
			</#list>
		</ReturnReasonCodeInfo9100>
		<CorrectReasonCodeInfo9100>
			<DataType>${cfx.MSG.CorrectReasonCodeInfo9100.DataType}</DataType>
			<UpdateNum>${cfx.MSG.CorrectReasonCodeInfo9100.UpdateNum}</UpdateNum>
			<#list cfx.MSG.CorrectReasonCodeInfo9100.CorrectReasonCode107 as var7>
			<CorrectReasonCode107>
				<ReasonCode>${var7.ReasonCode}</ReasonCode>
				<Description>${var7.Description}</Description>
				<OperSign>${var7.OperSign}</OperSign>
				<EffectDate>${var7.EffectDate}</EffectDate>
				<UpdateArea>${var7.UpdateArea}</UpdateArea>
			</CorrectReasonCode107>
			</#list>
		</CorrectReasonCodeInfo9100>
		<TaxTypeInfo9100>
			<DataType>${cfx.MSG.TaxTypeInfo9100.DataType}</DataType>
			<UpdateNum>${cfx.MSG.TaxTypeInfo9100.UpdateNum}</UpdateNum>
			<#list cfx.MSG.TaxTypeInfo9100.TaxTypeCode108 as var8>
			<TaxTypeCode108>
				<TaxTypeCode>${var8.TaxTypeCode}</TaxTypeCode>
				<TaxOrgType>${var8.TaxOrgType}</TaxOrgType>
				<TaxTypeName>${var8.TaxTypeName}</TaxTypeName>
				<Description>${var8.Description}</Description>
				<OperSign>${var8.OperSign}</OperSign>
				<EffectDate>${var8.EffectDate}</EffectDate>
				<UpdateArea>${var8.UpdateArea}</UpdateArea>
			</TaxTypeCode108>
			</#list>
		</TaxTypeInfo9100>
		<TaxSubjectInfo9100>
			<DataType>${cfx.MSG.TaxSubjectInfo9100.DataType}</DataType>
			<UpdateNum>${cfx.MSG.TaxSubjectInfo9100.UpdateNum}</UpdateNum>
			<#list cfx.MSG.TaxSubjectInfo9100.TaxSubjectCode109 as var9>
			<TaxSubjectCode109>
				<TaxSubjectCode>${var9.TaxSubjectCode}</TaxSubjectCode>
				<TaxOrgType>${var9.TaxOrgType}</TaxOrgType>
				<TaxSubjectName>${var9.TaxSubjectName}</TaxSubjectName>
				<Description>${var9.Description}</Description>
				<OperSign>${var9.OperSign}</OperSign>
				<EffectDate>${var9.EffectDate}</EffectDate>
				<UpdateArea>${var9.UpdateArea}</UpdateArea>
			</TaxSubjectCode109>
			</#list>
		</TaxSubjectInfo9100>
		<TaxTypeInfo9100>
			<DataType>${cfx.MSG.TaxTypeInfo9100.DataType}</DataType>
			<UpdateNum>${cfx.MSG.TaxTypeInfo9100.UpdateNum}</UpdateNum>
			<#list cfx.MSG.TaxTypeInfo9100.TaxTypeCode108 as var10>
			<TaxTypeCode108>
				<TaxTypeCode>${var10.TaxTypeCode}</TaxTypeCode>
				<TaxOrgType>${var10.TaxOrgType}</TaxOrgType>
				<TaxTypeName>${var10.TaxTypeName}</TaxTypeName>
				<Description>${var10.Description}</Description>
				<OperSign>${var10.OperSign}</OperSign>
				<EffectDate>${var10.EffectDate}</EffectDate>
				<UpdateArea>${var10.UpdateArea}</UpdateArea>
			</TaxTypeCode108>
			</#list>
		</TaxTypeInfo9100>
	</MSG>
</CFX>
	