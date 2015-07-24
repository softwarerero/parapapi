package py.suncom.parapapi;

import play.templates.JavaExtensions;

import java.text.DecimalFormat;
import java.util.Formatter;
import play.i18n.Lang;
import play.libs.I18N;

public class ParapapiExtensions extends JavaExtensions {

  public static String formatCurrency2(Number number, String currencyCode) {
    StringBuilder sb = new StringBuilder();
    Formatter formatter = new Formatter(sb, Lang.getLocale());
    String currencySymbol = I18N.getCurrencySymbol(currencyCode);
    return formatter.format(currencySymbol + " %,.0f", number).toString();
  }
}


