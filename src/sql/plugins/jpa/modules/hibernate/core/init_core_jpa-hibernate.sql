INSERT INTO core_admin_right(
            id_right, name, level_right, admin_url, description, is_updatable, 
            plugin_name, id_feature_group, icon_url, documentation_url, id_order)
    VALUES ('HIBERNATE_MONITOR', 'module.jpa.hibernate.monitor.adminFeature.title', 1, 
    		'jsp/admin/plugins/jpa/modules/hibernate/Monitor.jsp', 
    		'module.jpa.hibernate.monitor.adminFeature.description', 0, 
            'jpa-hibernate', 'SYSTEM', 'images/admin/skin/plugins/jpa/modules/hibernate/hibernate.png', '', 1);
            
INSERT INTO core_user_right (id_right,id_user) VALUES ('HIBERNATE_MONITOR',1);
