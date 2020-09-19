### ThreadLocal
每个thread中都存在一个map, map的类型是ThreadLocal.ThreadLocalMap. Map中的key为一个threadlocal实例. 这个Map使用了弱引用,不过弱引用只是针对key. 每个key都弱引用指向threadlocal. \
当把threadlocal实例置为null以后,没有任何强引用指向threadlocal实例,所以threadlocal将会被gc回收.\
但是,我们的value却不能回收,因为存在一条从current thread连接过来的强引用. 只有当前thread结束以后, current thread就不会存在栈中,强引用断开, Current Thread, Map, value将全部被GC回收.\
　　所以得出一个结论就是只要这个线程对象被gc回收，就不会出现内存泄露，但在threadLocal设为null和线程结束这段时间不会被回收的，就发生了我们认为的内存泄露。线程对象不被回收的情况，比如使用线程池的时候，线程结束是不会销毁的，会再次使用的,就可能出现内存泄露。\
在ThreadLocal的get,set的时候都会清除线程Map里所有key为null的value