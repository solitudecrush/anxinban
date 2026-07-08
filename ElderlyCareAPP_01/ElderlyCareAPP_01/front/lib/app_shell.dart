import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import 'screens/alerts_screen.dart';
import 'screens/charts_screen.dart';
import 'screens/home_screen.dart';
import 'screens/monitoring_screen.dart';
import 'screens/profile_screen.dart';
import 'state/nav_controller.dart';

class AppShell extends StatelessWidget {
  const AppShell({super.key});

  static const List<Widget> _pages = [
    HomeScreen(),
    ChartsScreen(),
    MonitoringScreen(),
    AlertsScreen(),
    ProfileScreen(),
  ];

  static const List<_NavItem> _items = [
    _NavItem(Icons.home_outlined, Icons.home, '首页'),
    _NavItem(Icons.show_chart_outlined, Icons.show_chart, '图表'),
    _NavItem(Icons.videocam_outlined, Icons.videocam, '监控'),
    _NavItem(Icons.notifications_none, Icons.notifications, '消息'),
    _NavItem(Icons.person_outline, Icons.person, '我的'),
  ];

  @override
  Widget build(BuildContext context) {
    final nav = context.watch<NavController>();
    return Scaffold(
      body: IndexedStack(index: nav.index, children: _pages),
      bottomNavigationBar: Material(
        elevation: 8,
        shadowColor: Colors.black12,
        child: Container(
          decoration: BoxDecoration(
            color: Theme.of(context).colorScheme.surface,
            border: Border(
              top: BorderSide(color: Colors.grey.shade200, width: 0.5),
            ),
          ),
          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 6),
          child: SafeArea(
            top: false,
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: List.generate(_items.length, (i) {
                final item = _items[i];
                final selected = i == nav.index;
                return _NavBarItem(
                  item: item,
                  selected: selected,
                  onTap: () => context.read<NavController>().setTab(i),
                );
              }),
            ),
          ),
        ),
      ),
    );
  }
}

class _NavItem {
  const _NavItem(this.icon, this.activeIcon, this.label);
  final IconData icon;
  final IconData activeIcon;
  final String label;
}

class _NavBarItem extends StatelessWidget {
  const _NavBarItem({
    required this.item,
    required this.selected,
    required this.onTap,
  });

  final _NavItem item;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    final color = selected
        ? const Color(0xFF2C7DA0)
        : Colors.grey.shade500;

    return Expanded(
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 4),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              AnimatedScale(
                scale: selected ? 1.15 : 1.0,
                duration: const Duration(milliseconds: 300),
                curve: Curves.easeInOut,
                child: Icon(
                  selected ? item.activeIcon : item.icon,
                  color: color,
                  size: 24,
                ),
              ),
              const SizedBox(height: 2),
              AnimatedDefaultTextStyle(
                duration: const Duration(milliseconds: 300),
                curve: Curves.easeInOut,
                style: TextStyle(
                  color: color,
                  fontSize: 11,
                  fontWeight: selected ? FontWeight.w600 : FontWeight.normal,
                ),
                child: Text(item.label),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
