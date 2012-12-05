CREATE TABLE `agency_global` (
  `agency_global_id` varchar(255) NOT NULL,
  `agency_global_country` varchar(2048) NOT NULL,
  `agency_global_region` varchar(2048) NOT NULL,
  `agency_global_province` varchar(2048) NOT NULL,
  PRIMARY KEY (`agency_global_id`),
  KEY `FK_agency_global_agency_global_id_idx` (`agency_global_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `agency` (
  `agency_global_id` varchar(255) NOT NULL,
  `agency_id` varchar(255) NOT NULL DEFAULT '',
  `agency_name` varchar(255) NOT NULL,
  `agency_url` varchar(2048) NOT NULL,
  `agency_timezone` varchar(255) NOT NULL,
  `agency_lang` varchar(255) DEFAULT NULL,
  `agency_phone` varchar(255) DEFAULT NULL,
  `agency_fare_url` varchar(2048) DEFAULT NULL,
  PRIMARY KEY (`agency_global_id`,`agency_id`),
  KEY `FK_agency_agency_global_id_idx` (`agency_global_id`),
  CONSTRAINT `FK_agency_agency_global_id` FOREIGN KEY (`agency_global_id`) REFERENCES `agency_global` (`agency_global_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `calendar` (
  `service_id` varchar(255) NOT NULL,
  `monday` tinyint(1) NOT NULL,
  `tuesday` tinyint(1) NOT NULL,
  `wednesday` tinyint(1) NOT NULL,
  `thursday` tinyint(1) NOT NULL,
  `friday` tinyint(1) NOT NULL,
  `saturday` tinyint(1) NOT NULL,
  `sunday` tinyint(1) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `agency_global_id` varchar(255) NOT NULL,
  PRIMARY KEY (`agency_global_id`,`service_id`),
  KEY `FK_calendar_agency_global_id_idx` (`agency_global_id`),
  CONSTRAINT `FK_calendar_agency_global_id` FOREIGN KEY (`agency_global_id`) REFERENCES `agency_global` (`agency_global_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `calendar_dates` (
  `service_id` varchar(255) NOT NULL,
  `date` date NOT NULL,
  `exception_type` int(11) NOT NULL,
  `agency_global_id` varchar(255) NOT NULL,
  PRIMARY KEY (`agency_global_id`,`service_id`,`date`),
  KEY `FK_calendar_dates_agency_global_id_idx` (`agency_global_id`),
  CONSTRAINT `FK_calendar_dates_agency_global_id` FOREIGN KEY (`agency_global_id`) REFERENCES `agency_global` (`agency_global_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `fare_attributes` (
  `fare_id` varchar(255) NOT NULL,
  `price` float NOT NULL,
  `currency_type` varchar(255) NOT NULL,
  `payment_method` int(11) NOT NULL,
  `transfers` int(11) NOT NULL,
  `transfer_duration` int(11) DEFAULT NULL,
  `agency_global_id` varchar(255) NOT NULL,
  PRIMARY KEY (`agency_global_id`,`fare_id`),
  KEY `FK_fare_attributes_agency_global_id_idx` (`agency_global_id`),
  CONSTRAINT `FK_fare_attributes_agency_global_id` FOREIGN KEY (`agency_global_id`) REFERENCES `agency_global` (`agency_global_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `fare_rules` (
  `fare_id` varchar(255) NOT NULL,
  `route_id` varchar(255) DEFAULT NULL,
  `origin_id` varchar(255) DEFAULT NULL,
  `destination_id` varchar(255) DEFAULT NULL,
  `contains_id` varchar(255) DEFAULT NULL,
  `agency_global_id` varchar(255) NOT NULL,
  KEY `FK_fare_rules_agency_global_id_idx` (`agency_global_id`),
  CONSTRAINT `FK_fare_rules_agency_global_id` FOREIGN KEY (`agency_global_id`) REFERENCES `agency_global` (`agency_global_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `feed_info` (
  `feed_publisher_name` varchar(255) NOT NULL,
  `feed_publisher_url` varchar(2048) NOT NULL,
  `feed_lang` varchar(255) NOT NULL,
  `feed_start_date` date DEFAULT NULL,
  `feed_end_date` date DEFAULT NULL,
  `feed_version` varchar(255) DEFAULT NULL,
  `agency_global_id` varchar(255) NOT NULL,
  PRIMARY KEY (`agency_global_id`,`feed_publisher_name`),
  KEY `FK_feed_info_agency_global_id_idx` (`agency_global_id`),
  CONSTRAINT `FK_feed_info_agency_global_id` FOREIGN KEY (`agency_global_id`) REFERENCES `agency_global` (`agency_global_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `frequencies` (
  `trip_id` varchar(255) NOT NULL,
  `start_time` time NOT NULL,
  `end_time` time NOT NULL,
  `headway_secs` int(11) NOT NULL,
  `exact_times` int(11) DEFAULT NULL,
  `agency_global_id` varchar(255) NOT NULL,
  PRIMARY KEY (`agency_global_id`,`trip_id`,`start_time`),
  KEY `FK_frequencies_agency_global_id_idx` (`agency_global_id`),
  CONSTRAINT `FK_frequencies_agency_global_id` FOREIGN KEY (`agency_global_id`) REFERENCES `agency_global` (`agency_global_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `routes` (
  `route_id` varchar(255) NOT NULL,
  `agency_id` varchar(255) DEFAULT NULL,
  `route_short_name` varchar(255) NOT NULL,
  `route_long_name` varchar(2048) NOT NULL,
  `route_desc` varchar(4096) DEFAULT NULL,
  `route_type` int(11) NOT NULL,
  `route_url` varchar(2048) DEFAULT NULL,
  `route_color` varchar(6) DEFAULT NULL,
  `route_text_color` varchar(6) DEFAULT NULL,
  `agency_global_id` varchar(255) NOT NULL,
  PRIMARY KEY (`agency_global_id`,`route_id`),
  KEY `FK_ agency_global_id_idx` (`agency_global_id`),
  KEY `FK_routes_agency_global_id_idx` (`agency_global_id`),
  CONSTRAINT `FK_routes_agency_global_id` FOREIGN KEY (`agency_global_id`) REFERENCES `agency_global` (`agency_global_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `shapes` (
  `shape_id` varchar(255) NOT NULL,
  `shape_pt_lat` varchar(255) NOT NULL,
  `shape_pt_lon` varchar(255) NOT NULL,
  `shape_pt_sequence` int(11) NOT NULL,
  `shape_dist_traveled` float DEFAULT NULL,
  `agency_global_id` varchar(255) NOT NULL,
  KEY `FK_shapes_agency_global_id_idx` (`agency_global_id`),
  CONSTRAINT `FK_shapes_agency_global_id` FOREIGN KEY (`agency_global_id`) REFERENCES `agency_global` (`agency_global_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `stop_times` (
  `trip_id` varchar(255) NOT NULL,
  `arrival_time` time NOT NULL,
  `departure_time` varchar(255) NOT NULL,
  `stop_id` varchar(255) NOT NULL,
  `stop_sequence` varchar(255) NOT NULL,
  `stop_headsign` varchar(255) DEFAULT NULL,
  `pickup_type` int(11) DEFAULT NULL,
  `drop_off_type` int(11) DEFAULT NULL,
  `shape_dist_traveled` varchar(255) DEFAULT NULL,
  `agency_global_id` varchar(255) NOT NULL,
  KEY `FK_stop_times_agency_global_id_idx` (`agency_global_id`),
  CONSTRAINT `FK_stop_times_agency_global_id` FOREIGN KEY (`agency_global_id`) REFERENCES `agency_global` (`agency_global_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `stops` (
  `stop_id` varchar(255) NOT NULL,
  `stop_code` varchar(2048) DEFAULT NULL,
  `stop_name` varchar(255) NOT NULL,
  `stop_desc` varchar(2048) DEFAULT NULL,
  `stop_lat` varchar(255) NOT NULL,
  `stop_lon` varchar(255) NOT NULL,
  `zone_id` varchar(255) DEFAULT NULL,
  `stop_url` varchar(2048) DEFAULT NULL,
  `location_type` int(11) DEFAULT NULL,
  `parent_station` varchar(255) DEFAULT NULL,
  `stop_timezone` varchar(255) DEFAULT NULL,
  `wheelchair_boarding` int(11) DEFAULT NULL,
  `agency_global_id` varchar(255) NOT NULL,
  PRIMARY KEY (`stop_id`,`agency_global_id`),
  KEY `FK__idx` (`agency_global_id`),
  KEY `FK_stops_agency_global_id_idx` (`agency_global_id`),
  CONSTRAINT `FK_stops_agency_global_id` FOREIGN KEY (`agency_global_id`) REFERENCES `agency_global` (`agency_global_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `transfers` (
  `from_stop_id` varchar(255) NOT NULL,
  `to_stop_id` varchar(255) NOT NULL,
  `transfer_type` int(11) NOT NULL,
  `min_transfer_time` int(11) DEFAULT NULL,
  `agency_global_id` varchar(255) NOT NULL,
  PRIMARY KEY (`agency_global_id`,`from_stop_id`,`to_stop_id`,`transfer_type`),
  KEY `FK_transfers_agency_global_id_idx` (`agency_global_id`),
  CONSTRAINT `FK_transfers_agency_global_id` FOREIGN KEY (`agency_global_id`) REFERENCES `agency_global` (`agency_global_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `trips` (
  `route_id` varchar(255) NOT NULL,
  `service_id` varchar(255) NOT NULL,
  `trip_id` varchar(255) NOT NULL,
  `trip_headsign` varchar(255) DEFAULT NULL,
  `trip_short_name` varchar(255) DEFAULT NULL,
  `direction_id` int(11) DEFAULT NULL,
  `block_id` varchar(255) DEFAULT NULL,
  `shape_id` varchar(255) DEFAULT NULL,
  `agency_global_id` varchar(255) NOT NULL,
  PRIMARY KEY (`agency_global_id`,`trip_id`),
  KEY `FK_agency_global_id_idx` (`agency_global_id`),
  KEY `FK_trips_agency_global_id_idx` (`agency_global_id`),
  CONSTRAINT `FK_trips_agency_global_id` FOREIGN KEY (`agency_global_id`) REFERENCES `agency_global` (`agency_global_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `paths` (
  `shape_id` varchar(255) NOT NULL,
  `path` varchar(10000) DEFAULT NULL,
  `num_points` int(11) DEFAULT NULL,
  `tot_dist` float DEFAULT NULL,
  `agency_global_id` varchar(255) NOT NULL,
  PRIMARY KEY (`shape_id`,`agency_global_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;