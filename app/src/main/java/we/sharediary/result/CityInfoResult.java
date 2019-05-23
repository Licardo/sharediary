package we.sharediary.result;

import java.util.List;

/**
 * Created by zhanghao on 2016/11/6.
 */

public class CityInfoResult {


    private List<ResultsVo> results;

    public List<ResultsVo> getResults() {
        return results;
    }

    public void setResults(List<ResultsVo> results) {
        this.results = results;
    }

    public static class ResultsVo {
        /**
         * location : {"id":"WTMKQ069CCJ7","name":"杭州","country":"CN","path":"杭州,杭州,浙江,中国","timezone":"Asia/Shanghai","timezone_offset":"+08:00"}
         * now : {"text":"晴","code":"0","temperature":"31"}
         * last_update : 2019-05-22T17:05:00+08:00
         */

        private LocationVo location;
        private NowVo now;
        private String last_update;

        public LocationVo getLocation() {
            return location;
        }

        public void setLocation(LocationVo location) {
            this.location = location;
        }

        public NowVo getNow() {
            return now;
        }

        public void setNow(NowVo now) {
            this.now = now;
        }

        public String getLast_update() {
            return last_update;
        }

        public void setLast_update(String last_update) {
            this.last_update = last_update;
        }

        public static class LocationVo {
            /**
             * id : WTMKQ069CCJ7
             * name : 杭州
             * country : CN
             * path : 杭州,杭州,浙江,中国
             * timezone : Asia/Shanghai
             * timezone_offset : +08:00
             */

            private String id;
            private String name;
            private String country;
            private String path;
            private String timezone;
            private String timezone_offset;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getCountry() {
                return country;
            }

            public void setCountry(String country) {
                this.country = country;
            }

            public String getPath() {
                return path;
            }

            public void setPath(String path) {
                this.path = path;
            }

            public String getTimezone() {
                return timezone;
            }

            public void setTimezone(String timezone) {
                this.timezone = timezone;
            }

            public String getTimezone_offset() {
                return timezone_offset;
            }

            public void setTimezone_offset(String timezone_offset) {
                this.timezone_offset = timezone_offset;
            }
        }

        public static class NowVo {
            /**
             * text : 晴
             * code : 0
             * temperature : 31
             */

            private String text;
            private String code;
            private String temperature;

            public String getText() {
                return text;
            }

            public void setText(String text) {
                this.text = text;
            }

            public String getCode() {
                return code;
            }

            public void setCode(String code) {
                this.code = code;
            }

            public String getTemperature() {
                return temperature;
            }

            public void setTemperature(String temperature) {
                this.temperature = temperature;
            }
        }
    }
}
