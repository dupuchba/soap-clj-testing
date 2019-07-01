(ns tt.core)

(comment

  (do (require '[clj-http.client :as client])
      (require '[paos.service :as service])
      (require '[paos.wsdl :as wsdl])

      (defn parse-response [{:keys [status body] :as response} body-parser fail-parser]
        (assoc response
               :body
               (case status
                 200 (body-parser body)
                 500 (fail-parser body))))

      (let [soap-service (wsdl/parse "http://www.thomas-bayer.com/axis2/services/BLZService?wsdl")
            srv          (get-in soap-service ["BLZServiceSOAP11Binding" :operations "getBank"])
            soap-url     (get-in soap-service ["BLZServiceSOAP11Binding" :url])
            soap-headers (service/soap-headers srv)
            content-type (service/content-type srv)
            mapping      (service/request-mapping srv)
            context      (assoc-in mapping ["Envelope" "Body" "getBank" "blz" :__value] "28350000")
            body         (service/wrap-body srv context)
            resp-parser  (partial service/parse-response srv)
            fault-parser (partial service/parse-fault srv)]
        (-> soap-url
            (client/post {:content-type content-type
                          :body         body
                          :headers      (merge {} soap-headers)
                          :do-not-throw true})
            (parse-response resp-parser fault-parser))))


  (let [soap-service (wsdl/parse "http://www.thomas-bayer.com/axis2/services/BLZService?wsdl")
        srv          (get-in soap-service ["BLZServiceSOAP11Binding" :operations "getBank"])
        soap-url     (get-in soap-service ["BLZServiceSOAP11Binding" :url])
        content-type (service/content-type srv)
        headers      (service/soap-headers srv)
        mapping      (service/request-mapping srv)
        context      (assoc-in mapping ["Envelope" "Body" "getBank" "blz" :__value] "28350000")
        body         (service/wrap-body srv context)
        parse-fn     (partial service/parse-response srv)]
    (-> soap-url
        (client/post {:content-type content-type
                      :body         body
                      :headers      headers})
        :body
        parse-fn))

  (use 'clojure.repl)

  (dir service)

  (let [soap-service (wsdl/parse "http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?WSDL")
        srv          (get-in soap-service ["CountryInfoServiceSoapBinding" :operations "CountryName"])
        soap-url     (get-in soap-service ["CountryInfoServiceSoapBinding" :url])
        content-type (service/content-type srv)
        headers      (service/soap-headers srv)
        mapping      (service/request-mapping srv)
        context      (assoc-in mapping ["Envelope" "Body" "CountryName" "sCountryISOCode" :__value] "FR")
        body         (service/wrap-body srv nil)
        parse-fn     (partial service/parse-response srv)]
    (-> soap-url
        (client/post {:content-type content-type
                      :body         body
                      :headers      headers})
        :body
        soap-service)
    mapping)

  (let [soap-service (wsdl/parse "http://webservices.oorsprong.org/websamples.countryinfo/CountryInfoService.wso?WSDL")
        srv          (get-in soap-service ["CountryInfoServiceSoapBinding" :operations "CountryName"])
        soap-url     (get-in soap-service ["CountryInfoServiceSoapBinding" :url])
        content-type (service/content-type srv)
        headers      (service/soap-headers srv)
        mapping      (service/request-mapping srv)
        context      (assoc-in mapping ["Envelope" "Body" "CountryName" "sCountryISOCode" :__value] "FRA")
        body         (service/wrap-body srv context)
        parse-fn     (partial service/parse-response srv)]
    (-> soap-url
        (client/post {:content-type content-type
                      :body         body
                      :headers      headers})
        :body
        ))

  (mapcat #(if (map? %) (keys %) %) (tree-seq map? vals  {"CountryName" {"sCountryISOCode" {:__value {:__type "string"}}}}))



  ["ListOfLanguagesByCode"
   "ListOfCurrenciesByName"
   "ListOfCurrenciesByCode"
   "CountryCurrency"
   "CountryName"
   "ListOfCountryNamesGroupedByContinent"
   "CountryIntPhoneCode" "ListOfContinentsByCode"
   "FullCountryInfo"
   "FullCountryInfoAllCountries"
   "LanguageName"
   "CountryISOCode"
   "CountriesUsingCurrency"
   "ListOfLanguagesByName"
   "ListOfCountryNamesByName"
   "CountryFlag"
   "CapitalCity"
   "CurrencyName"
   "ListOfContinentsByName"
   "LanguageISOCode"
   "ListOfCountryNamesByCode"]


  )
