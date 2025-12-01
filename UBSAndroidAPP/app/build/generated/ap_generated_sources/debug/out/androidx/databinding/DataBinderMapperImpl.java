package androidx.databinding;

public class DataBinderMapperImpl extends MergedDataBinderMapper {
  DataBinderMapperImpl() {
    addMapper(new br.gov.sp.fatec.ubsandroidapp.DataBinderMapperImpl());
  }
}
