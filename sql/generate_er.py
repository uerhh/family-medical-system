import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
import matplotlib.patches as mpatches
from matplotlib.patches import FancyBboxPatch, Polygon
import numpy as np

# Set up Chinese font
plt.rcParams['font.sans-serif'] = ['Microsoft YaHei', 'SimHei', 'KaiTi']
plt.rcParams['axes.unicode_minus'] = False

fig, ax = plt.subplots(1, 1, figsize=(32, 24))
ax.set_xlim(0, 32)
ax.set_ylim(0, 24)
ax.axis('off')
ax.set_facecolor('#fafbfc')
fig.patch.set_facecolor('#fafbfc')

# Title
ax.text(16, 23.4, '家庭医疗系统 E-R 图 (Chen 模型)', fontsize=26, fontweight='bold',
        ha='center', va='center', color='#1a1a2e')
ax.text(16, 22.9, 'Family Medical System - Entity Relationship Diagram', fontsize=13,
        ha='center', va='center', color='#6c757d')

# ===== COLOR SCHEME =====
ENTITY_COLOR = '#4361ee'
ENTITY_EDGE = '#3a0ca3'
ATTR_COLOR = '#f72585'
ATTR_EDGE = '#b5179e'
REL_COLOR = '#fca311'
REL_EDGE = '#e76f51'
LINE_COLOR = '#495057'

# ===== HELPER FUNCTIONS =====

def draw_entity(ax, x, y, name, comment, width=3.2, height=0.7):
    """Draw entity rectangle"""
    rect = FancyBboxPatch((x - width/2, y - height/2), width, height,
                          boxstyle="round,pad=0.05", facecolor=ENTITY_COLOR,
                          edgecolor=ENTITY_EDGE, linewidth=2.5, alpha=0.92)
    ax.add_patch(rect)
    ax.text(x, y + 0.05, name, fontsize=10, fontweight='bold',
            ha='center', va='center', color='white', fontfamily='monospace')
    ax.text(x, y - 0.22, comment, fontsize=7.5, ha='center', va='center',
            color='#e0e0ff', style='italic')

def draw_attribute(ax, x, y, name, is_pk=False, is_multivalued=False):
    """Draw attribute ellipse"""
    w = len(name) * 0.12 + 0.5
    h = 0.32
    edge = ATTR_EDGE
    face = '#fce4ec' if not is_pk else '#fff3e0'
    lw = 2.0 if is_pk else 1.5

    if is_multivalued:
        ellipse = mpatches.Ellipse((x, y), w, h, facecolor=face,
                                    edgecolor=edge, linewidth=lw, linestyle='--')
    else:
        ellipse = mpatches.Ellipse((x, y), w, h, facecolor=face,
                                    edgecolor=edge, linewidth=lw)
    ax.add_patch(ellipse)

    fontweight = 'bold' if is_pk else 'normal'
    ax.text(x, y, name, fontsize=7.5, fontweight=fontweight,
            ha='center', va='center', color='#1a1a2e')

def draw_relationship(ax, x, y, name, width=1.8, height=0.7):
    """Draw relationship diamond"""
    diamond = Polygon([
        (x, y + height/2),
        (x + width/2, y),
        (x, y - height/2),
        (x - width/2, y)
    ], closed=True, facecolor=REL_COLOR, edgecolor=REL_EDGE, linewidth=2.2, alpha=0.92)
    ax.add_patch(diamond)
    ax.text(x, y, name, fontsize=8.5, fontweight='bold',
            ha='center', va='center', color='#1a1a2e')

def draw_line(ax, x1, y1, x2, y2, label='', color=LINE_COLOR, style='solid'):
    """Draw a line between two points with optional label"""
    ls = '-' if style == 'solid' else '--'
    ax.plot([x1, x2], [y1, y2], color=color, linewidth=1.5, linestyle=ls, zorder=0)
    if label:
        mx, my = (x1 + x2) / 2, (y1 + y2) / 2
        ax.text(mx + 0.15, my + 0.15, label, fontsize=9, fontweight='bold',
                ha='center', va='center', color='#d62828',
                bbox=dict(boxstyle='round,pad=0.15', facecolor='white',
                          edgecolor='#dee2e6', alpha=0.95))

def draw_cardinality(ax, x, y, text, offset=(0.2, 0.2)):
    """Draw cardinality label near a connection point"""
    ax.text(x + offset[0], y + offset[1], text, fontsize=10, fontweight='bold',
            ha='center', va='center', color='#d62828',
            bbox=dict(boxstyle='circle,pad=0.12', facecolor='#fff3cd',
                      edgecolor='#ffc107', linewidth=1.5))

# ===== LAYOUT POSITIONS =====
# Entity positions
entities = {
    'sys_user':           (16, 19.5, '系统用户', 'sys_user'),
    'doctor_info':        (6, 19.5, '医生信息', 'doctor_info'),
    'health_record':      (26, 19.5, '健康档案', 'health_record'),
    'health_indicator':   (6, 14, '健康指标', 'health_indicator'),
    'appointment':        (16, 14, '预约挂号', 'appointment'),
    'diagnosis':          (26, 14, '诊断记录', 'diagnosis'),
    'consultation':       (6, 8.5, '图文问诊', 'consultation'),
    'consultation_reply': (16, 8.5, '问诊回复', 'consultation_reply'),
    'chat_message':       (26, 8.5, '聊天消息', 'chat_message'),
    'notification':       (16, 3.5, '系统通知', 'notification'),
}

# Relationship positions (diamonds)
relationships = {
    'r_user_doctor':      (11, 19.5, '是'),
    'r_user_health':      (21, 19.5, '拥有'),
    'r_user_indicator':   (11, 14, '记录'),
    'r_user_appoint':     (16, 16.5, '预约'),
    'r_appoint_diag':     (21, 14, '产生'),
    'r_user_consult':     (11, 8.5, '问诊'),
    'r_consult_reply':    (11, 8.5, '回复'),
    'r_user_chat':        (21, 8.5, '发送'),
    'r_user_notif':       (16, 6, '通知'),
}

# Attribute positions (ellipses)
attributes = {
    # sys_user attributes
    'su_id':        (12.5, 21.5, 'id (PK)', True),
    'su_username':  (14.2, 21.8, 'username', False),
    'su_password':  (16, 21.8, 'password', False),
    'su_realname':  (17.8, 21.8, 'real_name', False),
    'su_phone':     (19.5, 21.5, 'phone', False),
    'su_role':      (16, 21.0, 'role', False),

    # doctor_info attributes
    'di_id':        (3.5, 21.5, 'id (PK)', True),
    'di_user_id':   (5, 21.8, 'user_id (FK)', False),
    'di_dept':      (6.8, 21.5, 'department', False),
    'di_title':     (8.2, 21.2, 'title', False),

    # health_record attributes
    'hr_id':        (23.5, 21.5, 'id (PK)', True),
    'hr_user_id':   (25, 21.8, 'user_id (FK)', False),
    'hr_blood':     (27, 21.5, 'blood_type', False),
    'hr_height':    (28.5, 21.2, 'height', False),

    # health_indicator attributes
    'hi_id':        (3.5, 16, 'id (PK)', True),
    'hi_user_id':   (5, 16.3, 'user_id (FK)', False),
    'hi_type':      (6.8, 16, 'indicator_type', False),
    'hi_value':     (8.5, 15.7, 'indicator_value', False),

    # appointment attributes
    'ap_id':        (13, 16.5, 'id (PK)', True),
    'ap_patient':   (14.5, 16.8, 'patient_id (FK)', False),
    'ap_doctor':    (17.5, 16.8, 'doctor_id (FK)', False),
    'ap_date':      (19, 16.5, 'appointment_date', False),

    # diagnosis attributes
    'dg_id':        (23.5, 16.5, 'id (PK)', True),
    'dg_patient':   (25, 16.8, 'patient_id (FK)', False),
    'dg_doctor':    (27, 16.5, 'doctor_id (FK)', False),
    'dg_appt':      (28.5, 16.2, 'appointment_id (FK)', False),

    # consultation attributes
    'cn_id':        (3.5, 10.5, 'id (PK)', True),
    'cn_patient':   (5, 10.8, 'patient_id (FK)', False),
    'cn_doctor':    (6.8, 10.5, 'doctor_id (FK)', False),
    'cn_title':     (8.5, 10.2, 'title', False),

    # consultation_reply attributes
    'cr_id':        (13, 10.5, 'id (PK)', True),
    'cr_consult':   (14.5, 10.8, 'consultation_id (FK)', False),
    'cr_sender':    (17.5, 10.5, 'sender_id (FK)', False),
    'cr_content':   (19, 10.2, 'content', False),

    # chat_message attributes
    'cm_id':        (23.5, 10.5, 'id (PK)', True),
    'cm_sender':    (25, 10.8, 'sender_id (FK)', False),
    'cm_receiver':  (27, 10.5, 'receiver_id (FK)', False),
    'cm_content':   (28.5, 10.2, 'content', False),

    # notification attributes
    'nt_id':        (13, 5.5, 'id (PK)', True),
    'nt_user':      (14.5, 5.8, 'user_id (FK)', False),
    'nt_title':     (17.5, 5.5, 'title', False),
    'nt_type':      (19, 5.2, 'type', False),
}

# ===== DRAW ALL ELEMENTS =====

# Draw entities
for key, (x, y, comment, name) in entities.items():
    draw_entity(ax, x, y, name, comment)

# Draw relationships
for key, (x, y, name) in relationships.items():
    draw_relationship(ax, x, y, name)

# Draw attributes
for key, (x, y, name, is_pk) in attributes.items():
    draw_attribute(ax, x, y, name, is_pk)

# ===== DRAW CONNECTION LINES =====

# Entity to attribute connections
attr_connections = [
    # sys_user
    ('su_id', 'sys_user'), ('su_username', 'sys_user'), ('su_password', 'sys_user'),
    ('su_realname', 'sys_user'), ('su_phone', 'sys_user'), ('su_role', 'sys_user'),
    # doctor_info
    ('di_id', 'doctor_info'), ('di_user_id', 'doctor_info'), ('di_dept', 'doctor_info'),
    ('di_title', 'doctor_info'),
    # health_record
    ('hr_id', 'health_record'), ('hr_user_id', 'health_record'), ('hr_blood', 'health_record'),
    ('hr_height', 'health_record'),
    # health_indicator
    ('hi_id', 'health_indicator'), ('hi_user_id', 'health_indicator'),
    ('hi_type', 'health_indicator'), ('hi_value', 'health_indicator'),
    # appointment
    ('ap_id', 'appointment'), ('ap_patient', 'appointment'), ('ap_doctor', 'appointment'),
    ('ap_date', 'appointment'),
    # diagnosis
    ('dg_id', 'diagnosis'), ('dg_patient', 'diagnosis'), ('dg_doctor', 'diagnosis'),
    ('dg_appt', 'diagnosis'),
    # consultation
    ('cn_id', 'consultation'), ('cn_patient', 'consultation'), ('cn_doctor', 'consultation'),
    ('cn_title', 'consultation'),
    # consultation_reply
    ('cr_id', 'consultation_reply'), ('cr_consult', 'consultation_reply'),
    ('cr_sender', 'consultation_reply'), ('cr_content', 'consultation_reply'),
    # chat_message
    ('cm_id', 'chat_message'), ('cm_sender', 'chat_message'), ('cm_receiver', 'chat_message'),
    ('cm_content', 'chat_message'),
    # notification
    ('nt_id', 'notification'), ('nt_user', 'notification'), ('nt_title', 'notification'),
    ('nt_type', 'notification'),
]

for attr_key, entity_key in attr_connections:
    atx, aty = attributes[attr_key][:2]
    ex, ey = entities[entity_key][:2]
    draw_line(ax, atx, aty, ex, ey, color='#adb5bd', style='solid')

# Entity to relationship connections with cardinality
entity_rel_connections = [
    ('sys_user', 'r_user_doctor', '1'),
    ('doctor_info', 'r_user_doctor', '1'),
    ('sys_user', 'r_user_health', '1'),
    ('health_record', 'r_user_health', '1'),
    ('sys_user', 'r_user_indicator', '1'),
    ('health_indicator', 'r_user_indicator', 'N'),
    ('sys_user', 'r_user_appoint', '1'),
    ('appointment', 'r_user_appoint', 'N'),
    ('appointment', 'r_appoint_diag', '1'),
    ('diagnosis', 'r_appoint_diag', '1'),
    ('sys_user', 'r_user_consult', '1'),
    ('consultation', 'r_user_consult', 'N'),
    ('consultation', 'r_consult_reply', '1'),
    ('consultation_reply', 'r_consult_reply', 'N'),
    ('sys_user', 'r_user_chat', '1'),
    ('chat_message', 'r_user_chat', 'N'),
    ('sys_user', 'r_user_notif', '1'),
    ('notification', 'r_user_notif', 'N'),
]

for entity_key, rel_key, cardinality in entity_rel_connections:
    ent_x, ent_y = entities[entity_key][:2]
    rel_x, rel_y = relationships[rel_key][:2]
    draw_line(ax, ent_x, ent_y, rel_x, rel_y, color=LINE_COLOR)
    # Place cardinality near the entity end
    cx = ent_x + (rel_x - ent_x) * 0.2
    cy = ent_y + (rel_y - ent_y) * 0.2
    draw_cardinality(ax, cx, cy, cardinality)

# ===== LEGEND =====
legend_x, legend_y = 28, 3.5

legend_bg = FancyBboxPatch((legend_x - 1.5, legend_y - 2.2), 4.5, 3.0,
                            boxstyle="round,pad=0.15", facecolor='white',
                            edgecolor='#dee2e6', linewidth=1.5, alpha=0.98)
ax.add_patch(legend_bg)

ax.text(legend_x + 0.75, legend_y + 0.45, '图例 Legend', fontsize=12, fontweight='bold',
        ha='center', va='center', color='#1a1a2e')

# Entity legend
rect = FancyBboxPatch((legend_x - 1.2, legend_y - 0.1), 0.5, 0.35,
                        boxstyle="round,pad=0.03", facecolor=ENTITY_COLOR,
                        edgecolor=ENTITY_EDGE, linewidth=2)
ax.add_patch(rect)
ax.text(legend_x - 0.5, legend_y + 0.07, '实体 Entity', fontsize=9, va='center', color='#1a1a2e')

# Relationship legend
diamond = Polygon([
    (legend_x + 0.75, legend_y + 0.07 + 0.2),
    (legend_x + 0.75 + 0.25, legend_y + 0.07),
    (legend_x + 0.75, legend_y + 0.07 - 0.2),
    (legend_x + 0.75 - 0.25, legend_y + 0.07)
], closed=True, facecolor=REL_COLOR, edgecolor=REL_EDGE, linewidth=2)
ax.add_patch(diamond)
ax.text(legend_x + 1.2, legend_y + 0.07, '关系 Relationship', fontsize=9, va='center', color='#1a1a2e')

# Attribute legend
ellipse = mpatches.Ellipse((legend_x - 0.95, legend_y - 0.55), 0.5, 0.3,
                             facecolor='#fce4ec', edgecolor=ATTR_EDGE, linewidth=1.5)
ax.add_patch(ellipse)
ax.text(legend_x - 0.5, legend_y - 0.55, '属性 Attribute', fontsize=9, va='center', color='#1a1a2e')

# PK attribute legend
ellipse_pk = mpatches.Ellipse((legend_x + 0.75, legend_y - 0.55), 0.5, 0.3,
                                facecolor='#fff3e0', edgecolor=ATTR_EDGE, linewidth=2)
ax.add_patch(ellipse_pk)
ax.text(legend_x + 1.2, legend_y - 0.55, '主键属性 PK', fontsize=9, va='center', color='#1a1a2e')

# Cardinality legend
ax.text(legend_x - 1.2, legend_y - 1.1, '基数 Cardinality:', fontsize=9, fontweight='bold',
        va='center', color='#1a1a2e')
ax.text(legend_x - 1.2, legend_y - 1.45, '1 = 一的一方', fontsize=8, va='center', color='#495057')
ax.text(legend_x - 1.2, legend_y - 1.75, 'N = 多的一方', fontsize=8, va='center', color='#495057')

# Save
output_path = r'C:\Users\33559\Desktop\family_medical_er_diagram.png'
plt.savefig(output_path, dpi=200, bbox_inches='tight', pad_inches=0.5, facecolor='#fafbfc')
plt.close()
print(f'E-R diagram saved to: {output_path}')
